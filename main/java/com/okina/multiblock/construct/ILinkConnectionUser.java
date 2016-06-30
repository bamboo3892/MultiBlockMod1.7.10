package com.okina.multiblock.construct;

public interface ILinkConnectionUser {

	boolean canStartAt(int side);

	boolean tryConnect(ProcessorContainerTileEntity tile, int clickedSide, int linkUserSide);

}
