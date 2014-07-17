/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.CauseGIBDD;

import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.ServiceLocator;
import com.haulmont.cuba.gui.UserSessionClient;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.data.*;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.filestorage.WebExportDisplay;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.gui.components.WebFilter;
import com.haulmont.cuba.web.gui.components.WebPopupButton;
import com.haulmont.docflow.core.app.CardService;
import com.haulmont.docflow.core.app.NumerationService;
import com.haulmont.docflow.core.entity.Doc;
import com.haulmont.docflow.core.entity.NumeratorType;
import com.haulmont.docflow.web.DocflowAppWindow;
import com.haulmont.docflow.web.actions.ThesisExcelAction;
import com.haulmont.docflow.web.ui.common.DocCreator;
import com.haulmont.docflow.web.ui.common.DocflowHelper;
import com.haulmont.ext.core.entity.CauseGIBDD;
import com.haulmont.taskman.core.app.TaskmanService;
import com.haulmont.taskman.web.ui.common.CardTreeFrame;
import com.haulmont.taskman.web.ui.common.RemoveCardNullChildAction;
import com.haulmont.taskman.web.ui.task.ProcessMenuBuilderAction;
import com.haulmont.workflow.core.app.WfService;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.Card;
import com.haulmont.workflow.core.entity.CardInfo;
import com.haulmont.workflow.web.ui.base.ResolutionsFrame;
import com.vaadin.terminal.ThemeResource;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Created by mahdi on 7/1/14.
 */

public class CauseGIBDDBrowser extends  AbstractLookup {

    @Named("docsTable.edit")
    protected Action editAction;

    @Inject
    protected GroupTable docsTable;
    @Inject
    protected GroupDatasource<CauseGIBDD, UUID> docsDs;
    @Inject
    protected WebFilter genericFilter;
    @Inject
    private java.util.List<CardInfo> infoList;

    @Inject
    protected ResolutionsFrame resolutionsFrame;

    @Inject
    protected CheckBox hideResolutions;

    @Inject
    protected PopupButton createButton;

    @Inject
    protected TaskmanService taskman_TaskmanService;

    @Inject
    protected CardService docflow_CardService;

    protected List<Card> cards;

    protected CardTreeFrame cardTreeFrame;

    private boolean isTemplate;

    protected String entityName;

    protected CheckBox showResolutions;

    protected Tabsheet tabsheet;

    protected CreateAction createAction;

    private static final String HIDE_RESOLUTIONS = "hideResolutions";

    public CauseGIBDDBrowser(IFrame frame) {
        super(frame);
        entityName = MetadataProvider.getSession().getClass(MetadataProvider.getReplacedClass(CauseGIBDD.class)).getName();
    }
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        //установка возможности выбрать несколько карточек:
        docsTable.setMultiSelect(true);
        isTemplate = false;
        TableActionsHelper helper = new TableActionsHelper(this, docsTable);
        helper.createRefreshAction();
        //добавление обработчика событий, в котором будет происходить проверка на связь удаляемой карточки со связанными с нею карточками
        //при подтверждении удаления карточка будет удалена, а в связанной карточке будет удалена связь.
        docsTable.addAction(new CreateAction());
        docsTable.addAction(new RemoveCardNullChildAction("remove", this, docsTable));

        docsTable.addAction(new EditAction());

        docsTable.addAction(new RefreshAction(docsTable));
        docsTable.addAction(new ThesisExcelAction(docsTable,
                new WebExportDisplay(), genericFilter));
        //добавление обработчика событий по удалению просмотренных оповещений
        docsTable.addAction(new DeleteNotification());

        cards = docflow_CardService.getCardsForCurrentUserActor();
        final Action removeAction = docsTable.getAction("remove");
        final TaskmanService taskmanService = ServiceLocator.lookup(TaskmanService.NAME);
        docsDs.addListener(new CollectionDsListenerAdapter() {
            @Override
            public void itemChanged(Datasource ds, Entity prevItem, Entity item) {
                super.itemChanged(ds, prevItem, item);

                if (editAction != null && editAction.getOwner() != null)
                    ((Button) editAction.getOwner()).setEnabled(item != null);

                if (item == null) return;

                // ADMINISTRATOR_ROLE can remove any task, the CREATOR,
                // NOT INITIATOR only not sent on process
                Card card = (Card) ds.getItem();
                User subCreator = card.getSubstitutedCreator();
                User currentUser = UserSessionClient.getUserSession().getCurrentOrSubstitutedUser();
                Set<Card> selected = docsTable.getSelected();
                boolean allowRemove = true;
                if (selected.size() > 1) {
                    for (Card c : selected) {
                        if (taskmanService.isCurrentUserAdministrator()) {
                            break;
                        } else if (!(currentUser.equals(c.getSubstitutedCreator()) && (StringUtils.isBlank(c.getState()) || WfUtils.isCardInState(c, "New")))) {
                            allowRemove = false;
                            break;
                        }
                    }
                    removeAction.setEnabled(allowRemove);
                    if (removeAction.getOwner() != null)
                        ((Button) removeAction.getOwner()).setEnabled(allowRemove);
                } else {
                    allowRemove = taskmanService.isCurrentUserAdministrator() ||
                            (
                                    currentUser.equals(subCreator) &&
                                            (StringUtils.isBlank(((Card) ds.getItem()).getState()) || WfUtils.isCardInState((Card) ds.getItem(), "New"))
                            );
                    removeAction.setEnabled(allowRemove);
                    if (removeAction.getOwner() != null)
                        ((Button) removeAction.getOwner()).setEnabled(allowRemove);
                }

                if ((showResolutions != null) && BooleanUtils.isTrue((Boolean) showResolutions.getValue()) && (item == null || docsTable.getSelected().size() < 2)) {
                    String currentTab = tabsheet.getTab().getName();
                    if (currentTab.equals("resolutionsTab") && resolutionsFrame != null)
                        resolutionsFrame.setCard((Card) item);
                    if (currentTab.equals("hierarchyTab") && cardTreeFrame != null)
                        cardTreeFrame.setCard((Card) item);
                }
            }

            @Override
            public void stateChanged(Datasource ds, Datasource.State prevState, Datasource.State state) {
                if (removeAction != null && removeAction.getOwner() != null)
                    ((Button) removeAction.getOwner()).setEnabled(Datasource.State.VALID.equals(state) && ds.getItem() != null);
                if (editAction != null && editAction.getOwner() != null)
                    ((Button) editAction.getOwner()).setEnabled(Datasource.State.VALID.equals(state) && ds.getItem() != null);
            }

            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation) {
                refresh();
            }
        });
        docsTable.getDatasource().addListener(new CollectionDsListenerAdapter() {
            public void collectionChanged(CollectionDatasource ds, CollectionDatasourceListener.Operation operation) {
                loadCardInfoList();
            }
        });

        docsTable.addAction(new AbstractAction("createDoc") {
            public void actionPerform(Component component) {
                Doc docPattern = (Doc) getDsContext().get("docsDs").getItem();
                if (docPattern != null) {
                    LoadContext loadContext = new LoadContext(docPattern.getClass());
                    loadContext.setId(docPattern.getId());
                    loadContext.setView("copy");
                    docPattern = ServiceLocator.getDataService().load(loadContext);
                    Doc doc;
                    try {
                        doc = docPattern.getClass().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    doc.copyFrom(docPattern);
                    NumerationService ns = ServiceLocator.lookup(NumerationService.NAME);
                    if (StringUtils.isBlank(docPattern.getNumber()) && doc.getDocKind() != null
                            && NumeratorType.ON_CREATE.equals(doc.getDocKind().getNumeratorType())) {
                        String num = ns.getNextNumber(doc);
                        if (num != null)
                            doc.setNumber(num);
                    }
                    doc.setCreateDate(TimeProvider.currentTimestamp());
                    doc.setTemplate(false);
                    doc.setCreator(UserSessionClient.getUserSession().getUser());
                    doc.setSubstitutedCreator(UserSessionClient.getUserSession().getCurrentOrSubstitutedUser());
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("justCreated", true);
                    params.put("initialTemplate", docPattern);
                    Window editor = openEditor(entityName + ".edit", doc, WindowManager.OpenType.THIS_TAB, params);
                    editor.addListener(
                            new CloseListener() {
                                public void windowClosed(String actionId) {
                                    // refresh browser regardless of actionId as the new document
                                    // could be saved by DocCreator
                                    refresh();
                                }
                            }
                    );
                } else {
                    showNotification(getMessage("selectDocPattern.msg"), IFrame.NotificationType.HUMANIZED);
                }
            }

            @Override
            public String getCaption() {
                return getMessage("actions.createDoc");
            }

            @Override
            public boolean isVisible() {
                return super.isVisible() && isTemplate;
            }
        });

       /* if (createButton != null) {
            //проверка прав текущего пользователя на создание карточек типа
            //Счет на оплату. В зависимости от прав пользователя установка
            //возможности создавать карточку
            createButton.setEnabled(UserSessionProvider.getUserSession().isEntityOpPermitted(docsDs.getMetaClass(), EntityOp.CREATE));
            //добавление действия "Создать новую карточку"
            createButton.addAction(new CreateAction(docsTable) {
                //получение названий для данной кнопки из локализованного файла сообщений
                @Override
                public String getCaption() {
                    final String messagesPackage = getMessagesPack();
                    return MessageProvider.getMessage(messagesPackage, "actions.CreateNew");
                }
            });
            //добавление действия "Копировать существующую карточку"
            //createButton.addAction(new CopyAction());
        }    */

        //выделение строчек жирным если на них имеется уведомления
        //для текущего пользователя
        initTableColoring();
        docsDs.addListener(new DsListenerAdapter() {
            @Override
            public void stateChanged(Datasource ds, Datasource.State prevState,
                                     Datasource.State state) {
                super.stateChanged(ds, prevState, state);
                if (Datasource.State.VALID.equals(state)) {
                    loadCardInfoList();
                }
                refresh();
            }
        });



        final Card exclItem = (Card) params.get("exclItem");
        if (exclItem != null) {
            this.setLookupValidator(new Window.Lookup.Validator() {
                public boolean validate() {
                    Card selectedCard = docsTable.getSingleSelected();
                    CardService cardService = ServiceLocator.lookup(CardService.NAME);
                    if (selectedCard != null && cardService.isDescendant(exclItem, selectedCard,
                            MetadataProvider.getViewRepository().getView(Card.class, "_minimal"), "parentCard")) {
                        showNotification(MessageProvider.getMessage(CauseGIBDDBrowser.class, "cardIsChild"),
                                IFrame.NotificationType.WARNING);
                        return false;
                    }
                    return true;
                }
            });
        }

        addCommentColumn();
        addHasAttachmentColumn();
        refresh();
    }


    public void putParamsByCreateNew(Map<String, Object> params) {
        params.put("justCreated", true);
    }

    protected Doc initCurrentDoc(DocCreator creator) {
        return creator.getDocument();
    }

    protected class CreateAction extends AbstractAction {

        protected CreateAction() {
            super("create");
        }

        @Override
        public String getCaption() {
            return MessageProvider.getMessage(getClass(), "actions.CreateNew");
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled()
                    && UserSessionClient.getUserSession().isEntityOpPermitted(docsDs.getMetaClass(), EntityOp.CREATE);
        }

        public void actionPerform(Component component) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("docType", entityName);
            params.put("inTemplates", false);
            Object object = DocflowHelper.createDocEditorOrDocCreator(params);
            if (object instanceof DocCreator) {
                final DocCreator creator = (DocCreator) object;
                creator.addListener(
                        new CloseListener() {
                            public void windowClosed(String actionId) {
                                if (Editor.WINDOW_COMMIT.equals(actionId)) {

                                    Map<String, Object> params = new HashMap<String, Object>();
                                    params.put("initialTemplate", creator.getInitialTemplate());
                                    putParamsByCreateNew(params);
                                    Window editor = openEditor(entityName + ".edit", initCurrentDoc(creator), WindowManager.OpenType.THIS_TAB, params);
                                    editor.addListener(
                                            new CloseListener() {
                                                public void windowClosed(String actionId) {
                                                    // refresh browser regardless of actionId as the new document
                                                    // could be saved by DocCreator
                                                    refresh();
                                                }
                                            }
                                    );
                                }
                            }
                        }
                );
            }
            if (object instanceof Window) {
                final Window editor = (Window) object;
                editor.addListener(
                        new CloseListener() {
                            public void windowClosed(String actionId) {
                                // refresh browser regardless of actionId as the new document
                                // could be saved by DocCreator
                                refresh();
                            }
                        }
                );
            }
        }
    }

    protected void addHasAttachmentColumn() {
        MetaPropertyPath pp = docsDs.getMetaClass().getPropertyPath("hasAttachments");
        com.vaadin.ui.Table vTable = (com.vaadin.ui.Table) WebComponentsHelper.unwrap(docsTable);
        vTable.removeGeneratedColumn(pp);
        vTable.addGeneratedColumn(
                pp,
                new com.vaadin.ui.Table.ColumnGenerator() {
                    public com.vaadin.ui.Component generateCell(com.vaadin.ui.Table source, Object itemId, Object columnId) {
                        Doc doc = docsDs.getItem((UUID) itemId);
                        if (doc.getHasAttachments() != null && doc.getHasAttachments()) {
                            return new com.vaadin.ui.Embedded("", new ThemeResource("icons/attachment-small.png"));
                        }
                        return null;
                    }
                });
    }

    protected void refresh() {
        //обновление источник данных
        docsDs.refresh();
        //docsDs.getDsContext().refresh();
       /* docsTable.getDatasource().getDsContext().refresh();
        docsTable.getDatasource().refresh();
        docsTable.refresh();     */
        docsTable.getDatasource().refresh();
        loadCardInfoList();
    }

    //загрузка уведомлений
    protected void loadCardInfoList() {
        LoadContext lc = new LoadContext(CardInfo.class);
        LoadContext.Query q = lc
                .setQueryString("select ci from wf$CardInfo ci where ci.user.id" +
                        " = :userId");
        q.addParameter("userId", UserSessionProvider.getUserSession()
                .getCurrentOrSubstitutedUser());
        lc.setView("card-browse");
        infoList = ServiceLocator.getDataService().loadList(lc);
    }

    //добавление генератора стилей, который делает строчку жирной
    //если на нее имеется уведомление
    protected void initTableColoring() {
        com.vaadin.ui.Table vTable = (com.vaadin.ui.Table)
                WebComponentsHelper.unwrap(docsTable);
        vTable.setCellStyleGenerator(new com.vaadin.ui.Table.CellStyleGenerator() {

            public String getStyle(Object itemId, Object propertyId) {
                if (propertyId == null) {
                    if (!(itemId instanceof UUID))
                        return "";
                    CauseGIBDD cause =
                            docsDs.getItem((UUID)itemId);
                    return getTableCellStyle(cause);
                }
                return "";
            }
        });
    }

    protected String getTableCellStyle(Card card) {
        if (infoList != null) {
            if (WfUtils.isCardInState(card, "Obrabotan")) {
                return "taskfinished";
            }
            for (CardInfo cardInfo : infoList) {
                if (cardInfo.getCard().getId().equals(card.getId())
                        && cardInfo.getDeleteTs() == null) {
                    return "taskremind";
                }
            }
        }
        return "";
    }

    protected class EditAction extends AbstractAction {

        protected EditAction() {
            super("edit");
        }

        @Override
        public String getCaption() {
            final String messagesPackage = AppConfig.getMessagesPack();
            //если у текущего пользователя существуют права на изменение карточки,
            //кнопка называется изменить
            if (UserSessionProvider.getUserSession().isEntityOpPermitted(docsDs.getMetaClass(), EntityOp.UPDATE))
                return MessageProvider.getMessage(messagesPackage, "actions.Edit");
                //иначе кнопка называется "Просмотр"
            else
                return MessageProvider.getMessage(messagesPackage, "actions.View");
        }

        @Override
        public void actionPerform(Component component) {
            final Set selected = docsTable.getSelected();
            if (selected.size() == 1) {
                CauseGIBDD resolution = docsDs.getItem();
                CloseListener listener = new CloseListener() {
                    public void windowClosed(String actionId) {
                        //при закрытии окна редактирования необходимо
                        //обновить список счетов на оплату
                        refresh();
                    }
                };
                UserSettingService uss = ServiceLocator.lookup(UserSettingService.NAME);
                WindowManager.OpenType openType = "newTab".equals(uss.loadSetting("openEditorMode")) ?
                        WindowManager.OpenType.NEW_TAB : WindowManager.OpenType.THIS_TAB;
                String type = "";
                if (!isTemplate) type = ".edit";
                else type = ".templateEdit";

                Window window = frame.openEditor(entityName + type, resolution, openType,
                        Collections.<String, Object>singletonMap("closeListener", listener));
                window.addListener(listener);
            }
        }
    }

    /** Apply user settings to all components of this window */
    @Override
    public void applySettings(Settings settings) {
        boolean isLookup = (getFrame() instanceof Window.Lookup);
        if (!isLookup) {
            super.applySettings(settings);
        }
        initAfterSetLookup(isLookup);

        docsTable.removeAction(docsTable.getAction("refresh"));
        docsTable.removeAction(docsTable.getAction("excel"));
    }

    private void initAfterSetLookup(boolean isLookup) {
        final User user = UserSessionClient.getUserSession().getCurrentOrSubstitutedUser();
        SplitPanel splitPanel = getComponent("split");
        splitPanel.setSplitPosition(100);
        splitPanel.setLocked(true);
        if (isLookup) {
            docsTable.setMultiSelect(false);
        } else if (!isTemplate) {
//            final HashMap<UUID, com.vaadin.ui.Component> statesMap = new HashMap<UUID, com.vaadin.ui.Component>();
            final CardService cardService = ServiceLocator.lookup(CardService.NAME);
            addLocStateColumn();

            resolutionsFrame = getComponent("resolutionsFrame");
            resolutionsFrame.init();

            cardTreeFrame = getComponent("cardTreeFrame");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("card", null);
            cardTreeFrame.init(params);

            showResolutions = getComponent(HIDE_RESOLUTIONS);
            final SplitPanel split = getComponent("split");
            showResolutions.addListener(new ValueListener() {
                public void valueChanged(Object source, String property, Object prevValue, Object value) {
                    boolean showResolutionsValue = BooleanUtils.isTrue((Boolean) value);
                    int pos = (!showResolutionsValue ? 100 : 60);
                    split.setSplitPosition(pos);
                    split.setLocked(!showResolutionsValue);

                    Card card = (Card) docsTable.getSingleSelected();
                    if (showResolutionsValue && card != null) {
                        String currentTab = tabsheet.getTab().getName();
                        if (currentTab.equals("resolutionsTab"))
                            resolutionsFrame.setCard(card);
                        if (currentTab.equals("hierarchyTab"))
                            cardTreeFrame.setCard(card);
                    }
                }
            });

            String value = getSettings().get(HIDE_RESOLUTIONS).attributeValue("value");
            showResolutions.setValue(value == null ? false : BooleanUtils.toBoolean(value));
        } else {
            SplitPanel split = getComponent("split");
            split.setSplitPosition(100);
            split.setLocked(true);
        }
    }

    protected void addLocStateColumn() {
        MetaPropertyPath pp = docsDs.getMetaClass().getPropertyEx("locState");
        com.vaadin.ui.Table vTable = (com.vaadin.ui.Table) WebComponentsHelper.unwrap(docsTable);
        vTable.removeGeneratedColumn(pp);
        vTable.addGeneratedColumn(pp, new com.vaadin.ui.Table.ColumnGenerator() {
            public com.vaadin.ui.Component generateCell(com.vaadin.ui.Table source, Object itemId, Object columnId) {
                UUID uuid = (UUID) itemId;
                com.vaadin.ui.Component component;
                Doc doc = docsDs.getItem(uuid);
                boolean containsCard = cards != null ? cards.contains(doc) : false;
                if (doc != null && !WfUtils.isCardInStateList(doc, "Finished", "Canceled", "Obrabotan") &&
                        (containsCard || (!containsCard && WfUtils.isCardInState(doc, "New")))) {

                    final PopupButton popupButton = new WebPopupButton();
                    popupButton.setCaption(docsDs.getItem((UUID) itemId).getLocState());
                    int a = docsTable.getSelected().size();
                    popupButton.addAction(new AbstractAction("onChange") {
                        public void actionPerform(Component component) {
                            if (docsTable.getSelected().size() >= 1) {
                                popupButton.setEnabled(false);
                            } else popupButton.setEnabled(true);
                        }
                    });
                    component = WebComponentsHelper.unwrap(popupButton);
                    ((org.vaadin.hene.popupbutton.PopupButton) component).addListener(createProcessMenuBuilder(doc, docsTable, popupButton));
                    component.addStyleName("link");
                    component.addStyleName("dashed");
                } else {
                    component = new com.vaadin.ui.Label(doc == null ? "" : doc.getLocState(), com.vaadin.ui.Label.CONTENT_XHTML);
                }
                component.setWidth("-1px");
                return component;
            }
        });

    }

  /*  private void initAfterSetLookup(boolean isLookup) {
        //если экран открыт как экран с возможностью выбора экземпляров из списка
        //то панель с границей раздела (SplitPanel) растягивается на 100% и
        //отменяется возможность перемещения границы разделов
        if (isLookup) {
            SplitPanel split = getComponent("split");
            split.setSplitPosition(100);
            split.setLocked(true);
            docsTable.setMultiSelect(false);
        }
        //иначе добавляется генерируемая колонка
        else {
            ((com.vaadin.ui.Table) WebComponentsHelper.unwrap(docsTable)).addGeneratedColumn(docsDs.getMetaClass().getPropertyPath("locState"),
                    new com.vaadin.ui.Table.ColumnGenerator() {
                        public com.vaadin.ui.Component generateCell(com.vaadin.ui.Table source, Object itemId, Object columnId) {
                            UUID uuid = (UUID) itemId;
                            com.vaadin.ui.Component component;
                            Doc doc = docsDs.getItem(uuid);

                            boolean containsCard = cards != null ? cards.contains(doc) : false;
                            if (doc != null && !WfUtils.isCardInStateList(doc, "Finished", "Canceled") &&
                                    (containsCard || (!containsCard && WfUtils.isCardInState(doc, "New")))) {
                                //формируется кнопка, при нажатии на которую появляется выпадающий список с доступными
                                //для данной карточки действиями
                                final PopupButton popupButton = new WebPopupButton();
                                //получение списка состояний
                                popupButton.setCaption(docsDs.getItem((UUID) itemId).getLocState());

                                component = WebComponentsHelper.unwrap(popupButton);
                                ((org.vaadin.hene.popupbutton.PopupButton) component).addListener(new ProcessMenuBuilderAction(doc, docsTable, popupButton));
                                component.addStyleName("link");
                                component.addStyleName("dashed");
                            } else {
                                component = new com.vaadin.ui.Label(doc == null ? "" : doc.getLocState(), com.vaadin.ui.Label.CONTENT_XHTML);
                            }
                            component.setWidth("-1px");
                            return component;
                        }
                    });

            //инициализация журнала действий


            final SplitPanel split = getComponent("split");
            //добавление слушателя, который срабатывает при установлении или снятии галочки с опции "Скрыть журнал действий"
            /*hideResolutions.addListener(new ValueListener() {
                public void valueChanged(Object source, String property, Object prevValue, Object value) {
                    boolean hideResolutionsValue = BooleanUtils.isTrue((Boolean) value);
                    int pos = (hideResolutionsValue ? 100 : 60);
                    split.setSplitPosition(pos);
                    split.setLocked(hideResolutionsValue);

                    Card card = (Card) docsTable.getSingleSelected();



                }
            });

            String value = getSettings().get("hideResolutions").attributeValue("value");
            hideResolutions.setValue(value == null ? true : BooleanUtils.toBoolean(value));
        }
    }  */

    protected void addCommentColumn() {
        MetaPropertyPath pp = docsDs.getMetaClass().getPropertyPath("comment");
        com.vaadin.ui.Table vTable = (com.vaadin.ui.Table) WebComponentsHelper.unwrap(docsTable);
        vTable.removeGeneratedColumn(pp);
        vTable.addGeneratedColumn(
                pp,
                new com.vaadin.ui.Table.ColumnGenerator() {
                    public com.vaadin.ui.Component generateCell(com.vaadin.ui.Table source, Object itemId, Object columnId) {
                        UUID uuid = (UUID) itemId;
                        com.vaadin.ui.Component component;
                        Doc doc = docsDs.getItem(uuid);
                        String descr = doc.getComment();
                        int enterIdx = descr != null ? (descr.length() > 40 ? 40 : descr.indexOf('\n')) : -1;
                        //установка свойств отображаемого компонента
                        if (enterIdx != -1) {
                            com.vaadin.ui.TextField content = new com.vaadin.ui.TextField(null, descr);
                            content.setReadOnly(true);
                            content.setWidth("300px");
                            content.setHeight("300px");
                            component = new com.vaadin.ui.PopupView("<span>"
                                    + descr.substring(0, enterIdx) + "...</span>", content);
                            component.addStyleName("longtext");
                        } else {
                            component = new com.vaadin.ui.Label(descr == null ? "" : descr);
                        }

                        component.setWidth("-1px");
                        return component;
                    }
                });
    }

    protected class DeleteNotification extends AbstractAction {
        private static final long serialVersionUID = -4530817747247184231L;

        protected DeleteNotification() {
            super("deleteNotification");
        }

        public void actionPerform(Component component) {
            Set selected = docsTable.getSelected();
            if (selected.size() != 1) return;

            WfService wfService = ServiceLocator.lookup(WfService.NAME);
            User user = UserSessionProvider.getUserSession().getCurrentOrSubstitutedUser();
            wfService.deleteNotifications(docsDs.getItem(), user);

            refresh();
            //обновление папок приложение
            ((DocflowAppWindow) App.getInstance().getAppWindow()).reloadAppFolders(new ArrayList(selected));
        }
    }

   /* protected class CopyAction extends AbstractAction {

        protected CopyAction() {
            super("copy");
        }

        @Override
        public String getCaption() {
            return MessageProvider.getMessage(getClass(), "actions.Copy");
        }

        //делаем кнопку доступной, если у пользователя есть
        //права на создание данной карточки
        @Override
        public boolean isEnabled() {
            return super.isEnabled()
                    && UserSessionProvider.getUserSession().isEntityOpPermitted(docsDs.getMetaClass(), EntityOp.CREATE);
        }

        public void actionPerform(Component component) {
            //получение счета на оплату, который был выделен пользователем
            //в момент нажатия кнопки "Копировать"
            Call docs = docsDs.getItem();
            if (docs != null) {
                LoadContext loadContext = new LoadContext(docs.getClass());
                loadContext.setId(docs.getId());
                loadContext.setView("edit");
                loadContext.setUseSecurityConstraints(false);
                docs = ServiceLocator.getDataService().load(loadContext);

                Call doc;
                try {
                    //создаем новый документ Счет на оплату
                    doc = docs.getClass().newInstance();
                    doc.setTemplate(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //копирование значений полей в новую карточку
                doc.copyFrom(docs);
                doc.setName(docs.getName());
                doc.setPriority(docs.getPriority());
                doc.setExtClient(docs.getExtClient());
                doc.setCallDescription(docs.getCallDescription());
                doc.setNumber(docs.getNumber());
                doc.setFinishDatePlan(docs.getFinishDatePlan());
                doc.setConfirmRequired(docs.getConfirmRequired());
                doc.setReassignEnabled(docs.getReassignEnabled());
                doc.setRefuseEnabled(docs.getRefuseEnabled());
                doc.setCreator(UserSessionProvider.getUserSession().getUser());
                doc.setSubstitutedCreator(UserSessionProvider.getUserSession().getCurrentOrSubstitutedUser());

                Map<String, Object> params = new java.util.HashMap<String, Object>();
                params.put("justCreated", true);
                Window window = openEditor(entityName + ".edit", doc, WindowManager.OpenType.THIS_TAB, params);
                window.addListener(
                        new Window.CloseListener() {
                            public void windowClosed(String actionId) {
                                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                                    docsDs.refresh();
                                }
                            }
                        }
                );
            }
        }
    }        */

    protected ProcessMenuBuilderAction createProcessMenuBuilder(Doc doc, GroupTable docsTable, PopupButton popupButton) {
        return new ProcessMenuBuilderAction(doc, docsTable, popupButton);
    }
}
