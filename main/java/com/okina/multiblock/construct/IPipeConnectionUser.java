package com.okina.multiblock.construct;

public interface IPipeConnectionUser {

	/**Called by pipe when the pipe removed<br>
	 * Be sure to do check next tick, because the breaking pipe still exist.
	 */
	public void needUpdateEntry(/*int triger*/);

	/**1 : when parts added to this pipe network<br>
	 * 2 : when parts removed from this pipe network<br>
	 * 4 : <br>
	 * Each value can be added.
	 * @return policy value
	 */
	//	public int getUpdatePolicy();

}
