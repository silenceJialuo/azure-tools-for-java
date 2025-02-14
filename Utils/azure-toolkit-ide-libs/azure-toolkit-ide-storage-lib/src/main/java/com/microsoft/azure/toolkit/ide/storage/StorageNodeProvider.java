/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.ide.storage;

import com.microsoft.azure.toolkit.ide.common.IExplorerNodeProvider;
import com.microsoft.azure.toolkit.ide.common.action.ResourceCommonActionsContributor;
import com.microsoft.azure.toolkit.ide.common.component.AzureResourceLabelView;
import com.microsoft.azure.toolkit.ide.common.component.AzureServiceLabelView;
import com.microsoft.azure.toolkit.ide.common.component.Node;
import com.microsoft.azure.toolkit.ide.common.icon.AzureIcons;
import com.microsoft.azure.toolkit.lib.storage.AzureStorageAccount;
import com.microsoft.azure.toolkit.lib.storage.StorageAccount;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.microsoft.azure.toolkit.lib.Azure.az;

public class StorageNodeProvider implements IExplorerNodeProvider {
    private static final String NAME = "Storage Account";
    private static final String ICON = AzureIcons.StorageAccount.MODULE.getIconPath();

    @Nullable
    @Override
    public Object getRoot() {
        return az(AzureStorageAccount.class);
    }

    @Override
    public boolean accept(@Nonnull Object data, @Nullable Node<?> parent, ViewType type) {
        return data instanceof AzureStorageAccount || data instanceof StorageAccount;
    }

    @Nullable
    @Override
    public Node<?> createNode(@Nonnull Object data, @Nullable Node<?> parent, @Nonnull Manager manager) {
        if (data instanceof AzureStorageAccount) {
            final AzureStorageAccount service = ((AzureStorageAccount) data);
            final Function<AzureStorageAccount, List<StorageAccount>> accounts = asc -> asc.list().stream().flatMap(m -> m.storageAccounts().list().stream())
                .collect(Collectors.toList());
            return new Node<>(service).view(new AzureServiceLabelView<>(service, NAME, ICON))
                .actions(StorageActionsContributor.SERVICE_ACTIONS)
                .addChildren(accounts, (account, storageNode) -> this.createNode(account, storageNode, manager));
        } else if (data instanceof StorageAccount) {
            final StorageAccount account = (StorageAccount) data;
            return new Node<>(account)
                .view(new AzureResourceLabelView<>(account))
                .inlineAction(ResourceCommonActionsContributor.PIN)
                .doubleClickAction(StorageActionsContributor.OPEN_AZURE_STORAGE_EXPLORER)
                .actions(StorageActionsContributor.ACCOUNT_ACTIONS);
        }
        return null;
    }
}
