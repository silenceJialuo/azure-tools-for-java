/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.ide.vm;

import com.microsoft.azure.toolkit.ide.common.IActionsContributor;
import com.microsoft.azure.toolkit.ide.common.action.ResourceCommonActionsContributor;
import com.microsoft.azure.toolkit.ide.common.icon.AzureIcons;
import com.microsoft.azure.toolkit.lib.common.action.Action;
import com.microsoft.azure.toolkit.lib.common.action.ActionGroup;
import com.microsoft.azure.toolkit.lib.common.action.ActionView;
import com.microsoft.azure.toolkit.lib.common.action.AzureActionManager;
import com.microsoft.azure.toolkit.lib.common.action.IActionGroup;
import com.microsoft.azure.toolkit.lib.compute.virtualmachine.VirtualMachine;
import com.microsoft.azure.toolkit.lib.resource.ResourceGroup;

import java.util.Optional;

import static com.microsoft.azure.toolkit.lib.common.operation.OperationBundle.description;

public class VirtualMachineActionsContributor implements IActionsContributor {
    public static final int INITIALIZE_ORDER = ResourceCommonActionsContributor.INITIALIZE_ORDER + 1;

    public static final String SERVICE_ACTIONS = "actions.vm.service";
    public static final String VM_ACTIONS = "actions.vm.management";

    public static final Action.Id<VirtualMachine> ADD_SSH_CONFIG = Action.Id.of("vm.add_ssh_configuration");
    public static final Action.Id<VirtualMachine> CONNECT_SSH = Action.Id.of("vm.connect_ssh");
    public static final Action.Id<VirtualMachine> SFTP_CONNECTION =  Action.Id.of("vm.browse_files_sftp");
    public static final Action.Id<ResourceGroup> GROUP_CREATE_VM = Action.Id.of("group.create_vm");

    @Override
    public void registerActions(AzureActionManager am) {
        final ActionView.Builder addSshConfigView = new ActionView.Builder("Add/Edit SSH Configuration", AzureIcons.Action.ADD.getIconPath())
            .title(s -> Optional.ofNullable(s).map(r -> description("vm.add_ssh_config.vm", ((VirtualMachine) r).getName())).orElse(null))
            .enabled(s -> s instanceof VirtualMachine && ((VirtualMachine) s).getFormalStatus().isRunning());
        am.registerAction(ADD_SSH_CONFIG, new Action<>(ADD_SSH_CONFIG, addSshConfigView));

        final ActionView.Builder connectSshView = new ActionView.Builder("Connect Using SSH", AzureIcons.Action.CONSOLE.getIconPath())
                .title(s -> Optional.ofNullable(s).map(r -> description("vm.connect_using_ssh.vm", ((VirtualMachine) r).getName())).orElse(null))
                .enabled(s -> s instanceof VirtualMachine && ((VirtualMachine) s).getFormalStatus().isRunning());
        am.registerAction(CONNECT_SSH, new Action<>(CONNECT_SSH, connectSshView));

        final ActionView.Builder sftpConnectionView = new ActionView.Builder("Browse Files Using SFTP", AzureIcons.Action.SFTP.getIconPath())
                .title(s -> Optional.ofNullable(s).map(r -> description("vm.browse_files_sftp.vm", ((VirtualMachine) r).getName())).orElse(null))
                .enabled(s -> s instanceof VirtualMachine && ((VirtualMachine) s).getFormalStatus().isRunning());
        am.registerAction(SFTP_CONNECTION, new Action<>(SFTP_CONNECTION, sftpConnectionView));

        final ActionView.Builder createVmView = new ActionView.Builder("Virtual Machine")
            .title(s -> Optional.ofNullable(s).map(r -> description("vm.create_vm.group", ((ResourceGroup) r).getName())).orElse(null))
            .enabled(s -> s instanceof ResourceGroup);
        am.registerAction(GROUP_CREATE_VM, new Action<>(GROUP_CREATE_VM, createVmView));
    }

    @Override
    public void registerGroups(AzureActionManager am) {
        final ActionGroup serviceActionGroup = new ActionGroup(
            ResourceCommonActionsContributor.REFRESH,
            ResourceCommonActionsContributor.OPEN_AZURE_REFERENCE_BOOK,
            "---",
            ResourceCommonActionsContributor.CREATE
        );
        am.registerGroup(SERVICE_ACTIONS, serviceActionGroup);

        final ActionGroup accountActionGroup = new ActionGroup(
            ResourceCommonActionsContributor.PIN,
            "---",
            ResourceCommonActionsContributor.REFRESH,
            ResourceCommonActionsContributor.OPEN_AZURE_REFERENCE_BOOK,
            ResourceCommonActionsContributor.OPEN_PORTAL_URL,
            "---",
            VirtualMachineActionsContributor.ADD_SSH_CONFIG,
            VirtualMachineActionsContributor.CONNECT_SSH,
            VirtualMachineActionsContributor.SFTP_CONNECTION,
            "---",
            ResourceCommonActionsContributor.START,
            ResourceCommonActionsContributor.STOP,
            ResourceCommonActionsContributor.RESTART,
            ResourceCommonActionsContributor.DELETE
        );
        am.registerGroup(VM_ACTIONS, accountActionGroup);

        final IActionGroup group = am.getGroup(ResourceCommonActionsContributor.RESOURCE_GROUP_CREATE_ACTIONS);
        group.addAction(GROUP_CREATE_VM);
    }

    @Override
    public int getOrder() {
        return INITIALIZE_ORDER;
    }
}
