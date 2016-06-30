package com.okina.server.command;

import java.util.List;

import com.okina.main.TestCore;
import com.okina.network.CommandPacket;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

public class MBMCommand extends CommandBase {

	public MBMCommand() {}

	@Override
	public String getCommandName() {
		return "mbm";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 1;
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "Usage: /mbm <Subcommands> <value>. Commands are particleSpawnRate [0-100], renderPartsFancy {true, false}";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] commands) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		if(player != null){
			if(commands.length == 0){
				throw new WrongUsageException(getCommandUsage(commandSender));
			}else if("help".equals(commands[0])){
				throw new WrongUsageException(getCommandUsage(commandSender));
			}else if("particleSpawnRate".equals(commands[0])){
				try{
					int value = Integer.parseInt(commands[1]);
					if(value >= 0 && value <= 100){
						TestCore.proxy.sendCommandPacket(new CommandPacket(commands[0], commands[1]), player);
					}
				}catch (Exception e){
					throw new WrongUsageException(getCommandUsage(commandSender));
				}
			}else if("renderPartsFancy".equals(commands[0])){
				try{
					boolean value = Boolean.parseBoolean(commands[1]);
					TestCore.proxy.sendCommandPacket(new CommandPacket(commands[0], commands[1]), player);
				}catch (Exception e){
					throw new WrongUsageException(getCommandUsage(commandSender));
				}
			}else{
				throw new WrongUsageException(getCommandUsage(commandSender));
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] commands) {
		return commands.length == 1 ? getListOfStringsMatchingLastWord(commands, "particleSpawnRate", "renderPartsFancy") : (commands.length == 2 ? getListOfStringsMatchingLastWord(commands, "true", "false") : null);
	}

}
