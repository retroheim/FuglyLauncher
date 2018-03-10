/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.Parameter;

import lombok.Data;

/**
 * The command line arguments that the launcher accepts.
 */
@Data
public class LauncherArguments {

    @Parameter(names = "--dir")
    private File dir;

    @Parameter(names = "--bootstrap-version")
    private Integer bootstrapVersion;

    @Parameter(names = "--portable")
    private boolean portable;

    @Parameter(names = "--uripath")
    private String uriPath;

    @Parameter(names = "--run")
    private String run;

    public void processURI() {
    	String uripath = getUriPath();
    	if (!StringUtils.isEmpty(uripath)) {
	    	if (StringUtils.startsWith(uripath, "'")&&StringUtils.endsWith(uripath, "'"))
	    		uripath = StringUtils.substringBetween(uripath, "'");
	    	if (StringUtils.contains(uripath, "://"))
	    		uripath = StringUtils.substringAfter(uripath, "://");
	    	if (!StringUtils.isEmpty(uripath)) {
	        	String[] uripaths = StringUtils.split(uripath, "/");

	        	if (uripaths.length>0) {
	        		String type = uripaths[0];
	        		if (type.equals("run")) {
	        			if (uripaths.length>1) {
	        				String runid = uripaths[1];
	        				setRun(runid);
	        			}
	        		}
	        	}
	    	}
    	}
    }
}
