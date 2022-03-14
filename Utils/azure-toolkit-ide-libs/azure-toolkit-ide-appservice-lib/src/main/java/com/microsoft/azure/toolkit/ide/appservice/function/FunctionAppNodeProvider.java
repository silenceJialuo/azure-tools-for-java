/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.ide.appservice.function;

import com.microsoft.azure.toolkit.ide.appservice.file.AppServiceFileNode;
import com.microsoft.azure.toolkit.ide.appservice.function.node.FunctionsNode;
import com.microsoft.azure.toolkit.ide.common.IExplorerNodeProvider;
import com.microsoft.azure.toolkit.ide.common.component.AzureResourceLabelView;
import com.microsoft.azure.toolkit.ide.common.component.AzureServiceLabelView;
import com.microsoft.azure.toolkit.ide.common.component.Node;
import com.microsoft.azure.toolkit.lib.Azure;
import com.microsoft.azure.toolkit.lib.appservice.AzureAppService;
import com.microsoft.azure.toolkit.lib.appservice.function.FunctionApp;
import com.microsoft.azure.toolkit.lib.appservice.function.FunctionAppModule;
import com.microsoft.azure.toolkit.lib.appservice.model.AppServiceFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

import static com.microsoft.azure.toolkit.ide.appservice.webapp.WebAppNodeProvider.APP_SERVICE_ICON_PROVIDER;

public class FunctionAppNodeProvider implements IExplorerNodeProvider {
    private static final String NAME = "Function App";
    private static final String ICON = "/icons/functionapp.png";

    @Nullable
    @Override
    public Object getRoot() {
        return FunctionAppModule.class;
    }

    @Override
    public boolean accept(@Nonnull Object data, @Nullable Node<?> parent) {
        return data == FunctionAppModule.class ||
            data instanceof FunctionApp ||
            data instanceof AppServiceFile;
    }

    @Nullable
    @Override
    public Node<?> createNode(@Nonnull Object data, @Nullable Node<?> parent, @Nonnull Manager manager) {
        if (data == FunctionAppModule.class) {
            final AzureAppService service = Azure.az(AzureAppService.class);
            return new Node<>(service)
                .view(new AzureServiceLabelView<>(service, NAME, ICON))
                .actions(FunctionAppActionsContributor.SERVICE_ACTIONS)
                .addChildren(AzureAppService::functionApps, (d, p) -> this.createNode(d, p, manager));
        } else if (data instanceof FunctionApp) {
            final FunctionApp functionApp = (FunctionApp) data;
            return new Node<>(functionApp)
                .view(new AzureResourceLabelView<>(functionApp, FunctionApp::getStatus, APP_SERVICE_ICON_PROVIDER))
                .actions(FunctionAppActionsContributor.FUNCTION_APP_ACTIONS)
                .addChildren(Arrays::asList, (app, webAppNode) -> new FunctionsNode(app))
                .addChild(AppServiceFileNode::getRootFileNodeForAppService, (d, p) -> this.createNode(d, p, manager)) // Files
                .addChild(AppServiceFileNode::getRootLogNodeForAppService, (d, p) -> this.createNode(d, p, manager));
        } else if (data instanceof AppServiceFile) {
            final AppServiceFile file = (AppServiceFile) data;
            return new AppServiceFileNode(file);
        }
        return null;
    }
}
