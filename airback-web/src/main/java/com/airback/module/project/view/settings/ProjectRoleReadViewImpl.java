/**
 * Copyright © airback
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.module.project.view.settings;

import com.airback.common.i18n.SecurityI18nEnum;
import com.airback.form.view.LayoutType;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.SimpleProjectRole;
import com.airback.module.project.i18n.ProjectRoleI18nEnum;
import com.airback.module.project.i18n.RolePermissionI18nEnum;
import com.airback.module.project.ui.components.ProjectPreviewFormControlsGenerator;
import com.airback.security.PermissionFlag;
import com.airback.security.PermissionMap;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.HasPreviewFormHandlers;
import com.airback.vaadin.mvp.AbstractVerticalPageView;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.AbstractBeanFieldGroupViewFieldFactory;
import com.airback.vaadin.ui.FormContainer;
import com.airback.vaadin.ui.HeaderWithIcon;
import com.airback.vaadin.ui.IFormLayoutFactory;
import com.airback.vaadin.web.ui.AdvancedPreviewBeanForm;
import com.airback.vaadin.web.ui.DefaultReadViewLayout;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewComponent
public class ProjectRoleReadViewImpl extends AbstractVerticalPageView implements ProjectRoleReadView {
    private static final long serialVersionUID = 1L;

    private SimpleProjectRole beanItem;
    private AdvancedPreviewBeanForm<SimpleProjectRole> previewForm;
    private DefaultReadViewLayout previewLayout;
    private HeaderWithIcon headerText;
    private MHorizontalLayout headerLayout;

    private GridFormLayoutHelper projectFormHelper;

    public ProjectRoleReadViewImpl() {
        withMargin(true);
        headerText = HeaderWithIcon.h2(VaadinIcons.CLIPBOARD_USER, UserUIContext.getMessage(ProjectRoleI18nEnum.DETAIL));
        headerLayout = constructHeader();
        this.addComponent(headerLayout);

        previewForm = initPreviewForm();
        ComponentContainer actionControls = createButtonControls();
        headerLayout.with(actionControls).expand(actionControls);

        previewLayout = new DefaultReadViewLayout("");
        previewLayout.addBody(previewForm);
        this.addComponent(previewLayout);
    }

    protected AdvancedPreviewBeanForm<SimpleProjectRole> initPreviewForm() {
        return new AdvancedPreviewBeanForm<>();
    }

    protected ComponentContainer createButtonControls() {
        return (new ProjectPreviewFormControlsGenerator<>(previewForm)).createButtonControls(ProjectRolePermissionCollections.ROLES);
    }

    protected ComponentContainer createBottomPanel() {
        FormContainer permissionsPanel = new FormContainer();

        projectFormHelper = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.TWO_COLUMN);
        permissionsPanel.addSection(UserUIContext.getMessage(ProjectRoleI18nEnum.SECTION_PERMISSIONS), projectFormHelper.getLayout());

        return permissionsPanel;
    }

    protected void onPreviewItem() {
        projectFormHelper.getLayout().removeAllComponents();

        PermissionMap permissionMap = beanItem.getPermissionMap();
        for (int i = 0; i < ProjectRolePermissionCollections.PROJECT_PERMISSIONS.length; i++) {
            String permissionPath = ProjectRolePermissionCollections.PROJECT_PERMISSIONS[i];
            Enum permissionKey = RolePermissionI18nEnum.valueOf(permissionPath);
            Integer perVal = permissionMap.get(permissionKey.name());
            SecurityI18nEnum permissionVal = PermissionFlag.toVal(perVal);
            projectFormHelper.addComponent(new Label(UserUIContext.getMessage(permissionVal)),
                    UserUIContext.getMessage(permissionKey), UserUIContext.getMessage(permissionVal.desc()), i % 2, i / 2);
        }

    }

    protected String initFormTitle() {
        return beanItem.getRolename();
    }

    protected IFormLayoutFactory initFormLayoutFactory() {
        return new ProjectRoleFormLayoutFactory();
    }

    protected AbstractBeanFieldGroupViewFieldFactory<SimpleProjectRole> initBeanFormFieldFactory() {
        return new AbstractBeanFieldGroupViewFieldFactory<SimpleProjectRole>(previewForm) {
            private static final long serialVersionUID = 1L;

            @Override
            protected HasValue<?> onCreateField(Object propertyId) {
                return null;
            }
        };
    }

    @Override
    public SimpleProjectRole getItem() {
        return beanItem;
    }

    @Override
    public HasPreviewFormHandlers<SimpleProjectRole> getPreviewFormHandlers() {
        return previewForm;
    }

    private void initLayout() {
        ComponentContainer bottomPanel = createBottomPanel();
        if (bottomPanel != null) {
            previewLayout.addBottomControls(bottomPanel);
        }
    }

    private MHorizontalLayout constructHeader() {
        MHorizontalLayout container = new MHorizontalLayout().withMargin(false).withFullWidth();
        container.with(headerText).alignAll(Alignment.MIDDLE_LEFT).expand(headerText);
        return container;
    }

    public void previewItem(final SimpleProjectRole item) {
        beanItem = item;
        initLayout();
        previewLayout.setTitle(initFormTitle());

        previewForm.setFormLayoutFactory(initFormLayoutFactory());
        previewForm.setBeanFormFieldFactory(initBeanFormFieldFactory());
        previewForm.setBean(item);

        onPreviewItem();
    }

    public SimpleProjectRole getBeanItem() {
        return beanItem;
    }

    public AdvancedPreviewBeanForm<SimpleProjectRole> getPreviewForm() {
        return previewForm;
    }

}