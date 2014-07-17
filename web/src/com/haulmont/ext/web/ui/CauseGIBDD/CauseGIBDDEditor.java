/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.CauseGIBDD;


import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.ServiceLocator;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.*;
import com.haulmont.cuba.gui.data.impl.*;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.gui.components.*;
import com.haulmont.docflow.core.app.*;
import com.haulmont.docflow.core.entity.*;
import com.haulmont.docflow.web.DocflowAppWindow;
import com.haulmont.ext.core.entity.CauseGIBDD;
import com.haulmont.taskman.web.ui.common.*;
import com.haulmont.workflow.core.entity.*;
import com.haulmont.workflow.web.ui.base.AbstractCardEditor;
import com.haulmont.workflow.web.ui.base.CardProcFrame;
import com.haulmont.workflow.web.ui.base.CardRolesFrame;
import com.haulmont.workflow.web.ui.base.action.ActionsFrame;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by mahdi on 7/1/14.
 */

public class CauseGIBDDEditor extends AbstractCardEditor {

    protected CauseGIBDDAccessData accessData;

    protected boolean removedComment = false;

    protected boolean hierarchyTabInited = false;

    protected Boolean isTabComment;

    protected boolean historyTabInited = false;

    protected boolean versionsTabInitialized = false;

    @Inject
    protected CollectionDatasource<CardProc, UUID> cardProcDs;

    @Inject
    protected Datasource cardDs;

    @Inject
    protected PickerField parentCard;

    @Inject
    protected WebVBoxLayout startProc;

    @Inject
    protected CardProcFrame cardProcFrame;

    @Inject
    protected WebVBoxLayout buttonStart;

    @Inject
    protected CollectionPropertyDatasourceImpl cardProjectsDs;

    @Inject
    protected com.haulmont.cuba.core.app.DataService cuba_DataService;

    @Inject
    protected NumerationService docflow_NumerationService;

    public CauseGIBDDEditor(IFrame frame) {
        super(frame);
    }

    @Override
    protected boolean isCommentVisible() {
        return false;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        cardDs = getDsContext().get("CardDs");
        //после закрытия окна редактирования папки приложения должны обновиться

        /*telephoneNumber = getComponent("telephoneNumber");
        name = getComponent("name");
        extClient = getComponent("extClient");
        priority = getComponent("priority");
        finishDatePlan = getComponent("finishDatePlan");
        callDescription = getComponent("callDescription");   */
        // prevCall = getComponent("prevCall");

        addListener(new CloseListener() {
            public void windowClosed(String actionId) {
                App.getInstance().getAppWindow().getFoldersPane().reloadAppFolders();
            }
        });


        //получение источника данных, содержащего сущности комментариев
        //к карточкам
        final HierarchicalDatasource commentDs = ((IFrame) getComponent("cardCommentFrame")).getDsContext().get("commentDs");
        if (commentDs != null) {
            /*добавление слушателя, который следит за изменением
            количества комментариев к карточке
            если комментарии есть, то в название закладки
            добавляется число комментариев
            к данной карточке*/
            commentDs.addListener(new CollectionDsListenerAdapter() {
                public void collectionChanged(CollectionDatasource ds, Operation operation) {
                    for (Tabsheet.Tab tab : ((Tabsheet) getComponent("tabsheet")).getTabs()) {
                        if ("cardCommentTab".equals(tab.getName()) && !removedComment) {
                            int count = commentDs.size();
                            if (count > 0) {
                                tab.setCaption(getMessage("cardCommentTab") + " (" + count + ")");
                            } else {
                                tab.setCaption(getMessage("cardCommentTab"));
                            }
                        }
                    }
                }
            });
            //обновление источника данных
            commentDs.refresh();
        }

        //добавление слушателя, который следит за изменением
        //количества вложений в карточке
        //если вложения есть, то в название закладки
        //добавляется число вложений
        //в данной карточке
        attachmentsDs.addListener(new CollectionDsListenerAdapter() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation) {
                for (Tabsheet.Tab tab : ((Tabsheet) getComponent("tabsheet")).getTabs()) {
                    if (tab.getName() != null && tab.getName().equals("attachmentsTab")) {
                        if (ds.getItemIds().size() > 0)
                            tab.setCaption(getMessage("attachments") + " (" + ds.getItemIds().size() + ")");
                        else
                            tab.setCaption(getMessage("attachments"));
                    }
                }
            }

            @Override
            public void itemChanged(Datasource ds, Entity prevItem, Entity item) {
                for (Action action : attachmentsTable.getActions()) {
                    if (((Card) getItem()).getJbpmProcessId() != null && !"actions.Create".equals(action.getId()) &&
                            !"actions.Copy".equals(action.getId()))
                        action.setEnabled(item instanceof CardAttachment);
                }
            }
        });

        //добавление слушателя, который следит за изменением
        //количества проектов в карточке
        //если проекты выбраны, то в название закладки
        //добавляется число проектов
        //в данной карточке
        cardProjectsDs.addListener(new CollectionDsListenerAdapter() {
            public void collectionChanged(CollectionDatasource ds, Operation operation) {
                Tabsheet tabsheet = getComponent("tabsheet");
                for (Tabsheet.Tab tab : tabsheet.getTabs()) {
                    if ("cardProjectsTab".equals(tab.getName())) {
                        if (cardProjectsDs.size() > 0)
                            tab.setCaption(getMessage("cardProjectsTab") + " (" + cardProjectsDs.size() + ")");
                        else
                            tab.setCaption(getMessage("cardProjectsTab"));
                    }
                }
            }
        });

        //переопределение действия,которое будет происходить
        //при нажатии на кнопку поиска "родительской" карточки

        //удаление существующего обработчика событий
        /*parentCard.removeAction(parentCard.getAction(PickerField.LookupAction.NAME));
        //добавление нового обработчика событий
        parentCard.addAction(new PickerField.LookupAction(parentCard) {

            public void actionPerform(Component component) {
                if (DocTypeSelector.isNeeded("read")) {
                    //создание окна выбора типа карточки
                    //(Задача, Документ, Договор, Счет на оплату)
                    final DocTypeSelector docTypeSelector =
                            new DocTypeSelector("select", "read");
                    docTypeSelector.addListener(new com.vaadin.ui.Window.CloseListener() {
                        public void windowClose(com.vaadin.ui.Window.CloseEvent e) {
                            openCardLookup(docTypeSelector.getEntityName());
                        }
                    });
                    docTypeSelector.show();
                } else {
                    openCardLookup((getItem()).getMetaClass().getName());
                }
            }
        });   */
        //добавление обработчика событий
        //по открытию выбранной в качестве
        //основания карточки
        //  parentCard.addAction(new PickerField.OpenAction(parentCard));


        getDsContext().addListener(new DsContext.CommitListener() {
            //добавление слушателя, срабатывающего при
            //сохранении сущности в базу данных
            public void beforeCommit(CommitContext<Entity> context) {
                //получение сущности
                Doc doc = (Doc) getItem();
                //если сущность только что созданная
                if ((PersistenceHelper.isNew(doc))
                        //если пользователь
                        //не вводил номер счета
                        && StringUtils.isBlank(doc.getNumber())
                        //если нумератор для данной сущности
                        //существует и тип нумератора -
                        //при сохранении
                        && NumeratorType.ON_COMMIT.equals(doc.getDocKind().getNumeratorType())) {
                    //то получаем данный нумератор по имени
                    NumerationService ns = ServiceLocator.lookup(NumerationService.NAME);

                    //получение номер из последовательности
                    String num = ns.getNextNumber(doc);
                    if (num != null)
                        //с помощью метода доступа
                        //проставляем номер в документ
                        doc.setNumber(num);
                }

                CardSecurityFrame securityFrame = getComponent("securityFrame");
                if (securityFrame != null) {
                    context.getCommitInstances().addAll(securityFrame.getToCommit());
                }
            }

            @Override
            public void afterCommit(CommitContext<Entity> context, Set<Entity> result) {
                Set<Entity> rolesToCommit = new HashSet<Entity>();
                for (Entity entry : result) {
                    if (entry instanceof CardRole) {

                        rolesToCommit.add(entry);
                    }
                }
                CardRolesFrame frame = (CardRolesFrame) getComponent("cardProcFrame.cardRolesFrame");
                if (frame != null) {
                    frame.getActorActionsFieldsMap().clear();
                    CollectionDatasourceImpl tmpCardRolesDs = frame.getDsContext().get("tmpCardRolesDs");
                    if (tmpCardRolesDs != null)
                        tmpCardRolesDs.committed(rolesToCommit);
                }
                String template = TemplateHelper.processTemplate(getMessage("docEditor.caption"), Collections.<String, Object>singletonMap("item", getItem()));
                App.getInstance().getWindowManager().setCurrentWindowCaption((Window) getFrame(), template, null);
            }
        });

        //инициализация lazy-закладок
        initLazyTabs();
        isTabComment = BooleanUtils.isTrue((Boolean) params.get("isTabComment"));
        adjustForVersion(params);

    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);

        //получение сущности
        CauseGIBDD cause = (CauseGIBDD) getItem();

        initAttachments(cause);

        //блок для первоначальной инициализации новой карточки
        if (PersistenceHelper.isNew(cause)) {
            //установка создателя в карточку заявки
            cause.setCreator
                    (UserSessionProvider.getUserSession().getUser());
            //установка замещаемого создателя в карточку заявки
            cause.setSubstitutedCreator
                    (UserSessionProvider.getUserSession()
                            .getCurrentOrSubstitutedUser());

            //установка вида документа:
            LoadContext ctx = new LoadContext(DocKind.class);
            ctx.setQueryString("select dk from df$DocKind dk where dk.docType = " +
                    "(select dt from df$DocType dt where dt.name = :typeName)")
                    .addParameter("typeName", "ext$CauseGIBDD");
            ctx.setView("edit");
            DocKind docKind = cuba_DataService.load(ctx);
            cause.setDocKind(docKind);

            //установка времени
            cause.setDateTime(TimeProvider.currentTimestamp());

            //если нумератор, подключенный к данной карточке
            //имеет тип "При создании"
          /*  if (NumeratorType.ON_CREATE.equals(cause.getDocKind().getNumeratorType())) {
                //то получаем данный нумератор
                //получение номера из последовательности
                String num = docflow_NumerationService.getNextNumber(cause);
                if (num != null)
                    //с помощью метода доступа
                    //проставляем номер в документ
                    cause.setNumber(num);
            } */

            //проставление "родительской" карточки в текущую
            Card parentCard = ((Doc) item).getParentCard();
            if (parentCard != null) {
                cause.setParentCard(parentCard);
            }
        }

        //удаление нотификаций для данной карточки
        deleteNotifications(cause);
        //обновление папок приложений
        ((DocflowAppWindow) App.getInstance().getAppWindow()).reloadAppFolders(Collections.singletonList(cause));

        //получение процесса, доступного для данной карточки
        Proc process = cause.getProc();
        boolean isActiveProc = false;
        /*   AbstractWfAccessData */
        accessData = getContext().getParamValue("accessData");
        if (!(accessData == null || accessData.getStartCardProcessEnabled()))
            isActiveProc = true;
        else if (process != null)
            for (UUID uuid : cardProcDs.getItemIds()) {
                CardProc cpt = cardProcDs.getItem(uuid);
                if (cpt.getProc().getId().equals(process.getId())
                        && cpt.getActive()) {
                    isActiveProc = true;
                }
            }

        //если процесс активен для данной карточки
        if (!isActiveProc) {
            //делаем видимой панель с кнопками,
            //отражающими действия для данного процесса
            startProc.setVisible(true);
            CollectionDatasource<Proc, UUID> procDs = cardProcFrame
                    .getDsContext().get("procDs");
            if (procDs.getState() == Datasource.State.VALID) {
                for (UUID uuid : procDs.getItemIds()) {
                    final Proc proc = procDs.getItem(uuid);

                    //создание кнопки запуска процесса
                    Button button = new WebButton();
                    button.setWidth("200px");
                    button.setAction(new AbstractAction("start" +
                            proc.getJbpmProcessKey()) {
                        public void actionPerform(Component component) {
                            startProcess(proc);
                        }

                        @Override
                        public String getCaption() {
                            return proc.getName();
                        }
                    });
                    buttonStart.add(button);
                }
            }

        }

        if (isTabComment)
            ((Tabsheet) getComponent("tabsheet")).setTab("cardCommentTab");

    }

    //создание диалога подтверждения запуска
    //процесса
    private void startProcess(final Proc proc) {
        showOptionDialog(
                MessageProvider
                        .getMessage(cardProcFrame.getClass(), "runProc.title"),
                String.format(MessageProvider
                                .getMessage(cardProcFrame.getClass(), "runProc.msg"),
                        proc.getName()),
                IFrame.MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                CardProc cp = null;
                                for (UUID uuid : cardProcDs.getItemIds()) {
                                    CardProc cpt = cardProcDs.getItem(uuid);
                                    if (cpt.getProc().getId().equals(proc.getId())) {
                                        cp = cpt;
                                    }
                                }
                                if (cp == null) {
                                    cp = new CardProc();
                                    cp.setCard((CauseGIBDD) getItem());
                                    cp.setProc(proc);
                                    cp.setActive(false);
                                    cp.setSortOrder(cardProcFrame.calculateSortOrder());
                                    cardProcDs.addItem(cp);
                                    cardProcFrame.getCardRolesFrame()
                                            .procChanged(cp.getProc());
                                    cardProcFrame.getCardRolesFrame()
                                            .initDefaultActors(cp.getProc());
                                }
                                cardProcFrame.startProcess(cp);
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );

    }

    @Override
    protected void initActionsFrame(Card card, ActionsFrame actionsFrame) {
        actionsFrame.initActions(card, isCommentVisible());
    }

    private void openCardLookup(String entityName) {
        if (entityName == null) {
            return;
        }

        openLookup(entityName + ".browse", new Lookup.Handler() {
            public void handleLookup(Collection items) {
                if (items != null && items.size() > 0) {
                    ((Card) getItem()).setParentCard((Card) items.iterator().next());
                }
            }
        }, WindowManager.OpenType.THIS_TAB, Collections
                .<String, Object>singletonMap("exclItem", getItem()));
    }

    //инициализация  lazy-закладок
    private void initLazyTabs() {
        Tabsheet tabsheet = getComponent("tabsheet");
        accessData = getContext().getParamValue("accessData");
        tabsheet.addListener(new Tabsheet.TabChangeListener() {
            public void tabChanged(Tabsheet.Tab newTab) {
                if ("cardProjectsTab".equals(newTab.getName())) {
                    CardProjectsFrame cardProjectsFrame = getComponent("cardProjectsFrame");
                    cardProjectsFrame.init();
                    cardProjectsFrame.getComponent("add").setVisible(true);
                    cardProjectsFrame.getComponent("delete").setVisible(true);
                    if (!accessData.getNotVersion()) {
                        cardProjectsFrame.setEditable(false);
                    }
                } else if ("history".equals(newTab.getName())) {
                    initHistoryTab();
              /*  } else if ("hierarchy".equals(newTab.getName())) {
                    initHierarchyTab(); */
                } else if ("securityTab".equals(newTab.getName())) {
                    CardSecurityFrame securityFrame = getComponent("securityFrame");
                    securityFrame.init();
                    securityFrame.refreshDs();
                    securityFrame.setEditable(accessData.getNotVersion());
                }
            }
        });
    }

    private void initHistoryTab() {
        if (historyTabInited) {
            return;
        }

        Tabsheet tabsheet = getComponent("historyTabsheet");
        tabsheet.addListener(new Tabsheet.TabChangeListener() {
            public void tabChanged(Tabsheet.Tab newTab) {
                if ("versionsTab".equals(newTab.getName())) {
                    initVersionsTab();
                }
            }
        });

        historyTabInited = true;
    }

    protected void initVersionsTab() {
        if (versionsTabInitialized)
            return;

        Table versionsTable = getComponent("versionsTable");
        OpenVersionAction openVersionAction = new OpenVersionAction(versionsTable);
        versionsTable.addAction(openVersionAction);
        versionsTabInitialized = true;
    }

    protected class OpenVersionAction extends AbstractAction {

        private final Table versionsTable;

        public OpenVersionAction(Table versionsTable) {
            super("edit");
            this.versionsTable = versionsTable;
        }

        @Override
        public String getCaption() {
            return getMessage("open");
        }

        public void actionPerform(Component component) {
            Set selected = versionsTable.getSelected();
            if (selected.size() != 1)
                return;

            Doc docVersion = (Doc) selected.iterator().next();

            openEditor(versionsTable.getDatasource().getMetaClass().getName() + ".edit",
                    docVersion, WindowManager.OpenType.THIS_TAB);
        }
    }

    private void initHierarchyTab() {
        final CardTreeFrame cardTreeFrame = getComponent("cardTreeFrame");
        accessData = getContext().getParamValue("accessData");
        if (hierarchyTabInited) {
            cardTreeFrame.refreshDs();
            return;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("card", getItem());
        cardTreeFrame.init(params);
        cardTreeFrame.refreshDs();

        final CollectionDatasource cardRelationsDs = getDsContext().get("cardRelationsDs");
        final Tabsheet tabsheet = getComponent("hierarchyTabsheet");
        CardService cardService = ServiceLocator.lookup(CardService.NAME);
        Integer count = cardService.getCardRelationsCount((Card) getItem());
        if (count > 0) {
            tabsheet.getTab("cardRelationsTab").setCaption(getMessage("cardRelationsTab") + " (" + count + ")");
        } else {
            tabsheet.getTab("cardRelationsTab").setCaption(getMessage("cardRelationsTab"));
        }
        cardRelationsDs.addListener(new CollectionDsListenerAdapter() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation) {
                super.collectionChanged(ds, operation);
                Tabsheet.Tab tab = tabsheet.getTab("cardRelationsTab");
                if (tab != null) {
                    int count = cardRelationsDs.size();
                    if (count > 0) {
                        tab.setCaption(getMessage("cardRelationsTab") + " (" + count + ")");
                    } else {
                        tab.setCaption(getMessage("cardRelationsTab"));
                    }
                }
            }
        });

        tabsheet.addListener(new Tabsheet.TabChangeListener() {
            public void tabChanged(Tabsheet.Tab newTab) {
                if ("docTreeTab".equals(newTab.getName())) {
                    cardTreeFrame.refreshDs();
                } else if ("cardRelationsTab".equals(newTab.getName())) {
                    CardRelationsFrame cardRelationsFrame = getComponent("cardRelationsFrame");
                    cardRelationsFrame.init();
                    cardRelationsFrame.setEditable(accessData.getNotVersion());
                }
            }
        });

        hierarchyTabInited = true;
    }

    protected void refresh() {
        //обновление источник данных
        cardDs.refresh();
    }


    protected void adjustForVersion(Map<String, Object> params) {
        accessData = getContext().getParamValue("accessData");
        if (accessData != null) {
            Tabsheet ts = getComponent("tabsheet");
            Component resolutionsPane = getComponent("resolutionsPane");
            if (!accessData.getNotVersion()) {
                ts.removeTab("history");
                ts.removeTab("processTab");
                resolutionsPane.setVisible(false);
                setAttachmentButtonsEnabled(false);
                attachmentsDs.addListener(new CollectionDsListenerAdapter() {
                    @Override
                    public void itemChanged(Datasource ds, Entity prevItem, Entity item) {
                        setAttachmentButtonsEnabled(false);
                    }
                });
            }
        }
    }

    protected void setAttachmentButtonsEnabled(boolean enable) {
        String[] componentNames = {"createAttachBtn", "editAttach", "removeAttach", "pasteAttach"};
        for (String componentName : componentNames) {
            Component component = cardAttachmentsFrame.getComponent(componentName);
            if (component != null)
                component.setEnabled(false);
        }
    }
}