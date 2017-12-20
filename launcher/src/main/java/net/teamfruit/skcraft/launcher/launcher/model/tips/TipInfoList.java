package net.teamfruit.skcraft.launcher.launcher.model.tips;

import java.util.List;

import lombok.Data;

@Data
public class TipInfoList {

    public static final int MIN_VERSION = 1;

    private int minimumVersion;
	private List<TipInfo> tips;

}
