package com.okina.client;

import java.io.Serializable;

public class EffectProperties implements Serializable {

	/**0 - 100*/
	public int particleSpawnRate;
	public boolean renderPartsFancy;

	public EffectProperties() {
		particleSpawnRate = 100;
		renderPartsFancy = true;
	}

}
