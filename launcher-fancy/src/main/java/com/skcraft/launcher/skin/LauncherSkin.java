/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.skin;

import com.skcraft.launcher.skin.LauncherButtonShaper;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ColorSchemeSingleColorQuery;
import org.pushingpixels.substance.api.ColorSchemeTransform;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter;
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter;
import org.pushingpixels.substance.api.painter.border.StandardBorderPainter;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.SubstanceDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.painter.highlight.SubstanceHighlightPainter;
import org.pushingpixels.substance.api.shaper.SubstanceButtonShaper;
import org.pushingpixels.substance.api.skin.GraphiteSkin;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;

public class LauncherSkin
extends GraphiteSkin {
    public LauncherSkin() {
        SubstanceSkin.ColorSchemes schemes = SubstanceSkin.getColorSchemes("com/skcraft/launcher/skin/graphite.colorschemes");
        SubstanceColorScheme activeScheme = schemes.get("Graphite Active");
        SubstanceColorScheme selectedDisabledScheme = schemes.get("Graphite Selected Disabled");
        SubstanceColorScheme selectedScheme = schemes.get("Graphite Selected");
        SubstanceColorScheme disabledScheme = schemes.get("Graphite Disabled");
        SubstanceColorScheme enabledScheme = schemes.get("Graphite Enabled");
        SubstanceColorScheme backgroundScheme = schemes.get("Graphite Background");
        SubstanceColorScheme highlightScheme = schemes.get("Graphite Highlight");
        SubstanceColorScheme borderScheme = schemes.get("Graphite Border");
        SubstanceColorScheme separatorScheme = schemes.get("Graphite Separator");
        SubstanceColorScheme textHighlightScheme = schemes.get("Graphite Text Highlight");
        SubstanceColorScheme highlightMarkScheme = schemes.get("Graphite Highlight Mark");
        SubstanceColorScheme tabHighlightScheme = schemes.get("Graphite Tab Highlight");
        SubstanceColorSchemeBundle scheme = new SubstanceColorSchemeBundle(activeScheme, enabledScheme, disabledScheme);
        scheme.registerHighlightColorScheme(highlightScheme, 0.6f, ComponentState.ROLLOVER_UNSELECTED);
        scheme.registerHighlightColorScheme(highlightScheme, 0.8f, ComponentState.SELECTED);
        scheme.registerHighlightColorScheme(highlightScheme, 1.0f, ComponentState.ROLLOVER_SELECTED);
        scheme.registerHighlightColorScheme(highlightScheme, 0.75f, ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);
        scheme.registerColorScheme(highlightScheme, ColorSchemeAssociationKind.HIGHLIGHT_BORDER, ComponentState.getActiveStates());
        scheme.registerColorScheme(borderScheme, ColorSchemeAssociationKind.BORDER, new ComponentState[0]);
        scheme.registerColorScheme(separatorScheme, ColorSchemeAssociationKind.SEPARATOR, new ComponentState[0]);
        scheme.registerColorScheme(textHighlightScheme, ColorSchemeAssociationKind.TEXT_HIGHLIGHT, ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);
        scheme.registerColorScheme(highlightScheme, ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);
        scheme.registerColorScheme(highlightMarkScheme, ColorSchemeAssociationKind.HIGHLIGHT_MARK, ComponentState.getActiveStates());
        scheme.registerColorScheme(highlightMarkScheme, ColorSchemeAssociationKind.MARK, ComponentState.ROLLOVER_SELECTED, ComponentState.ROLLOVER_UNSELECTED);
        scheme.registerColorScheme(borderScheme, ColorSchemeAssociationKind.MARK, ComponentState.SELECTED);
        scheme.registerColorScheme(disabledScheme, 0.5f, ComponentState.DISABLED_UNSELECTED);
        scheme.registerColorScheme(selectedDisabledScheme, 0.65f, ComponentState.DISABLED_SELECTED);
        scheme.registerColorScheme(highlightScheme, ComponentState.ROLLOVER_SELECTED);
        scheme.registerColorScheme(selectedScheme, ComponentState.SELECTED);
        scheme.registerColorScheme(tabHighlightScheme, ColorSchemeAssociationKind.TAB, ComponentState.ROLLOVER_SELECTED);
        this.registerDecorationAreaSchemeBundle(scheme, backgroundScheme, DecorationAreaType.NONE);
        this.setSelectedTabFadeStart(0.1);
        this.setSelectedTabFadeEnd(0.3);
        this.buttonShaper = new LauncherButtonShaper();
        this.watermark = null;
        this.fillPainter = new FractionBasedFillPainter("Graphite", new float[]{0.0f, 0.5f, 1.0f}, new ColorSchemeSingleColorQuery[]{ColorSchemeSingleColorQuery.ULTRALIGHT, ColorSchemeSingleColorQuery.LIGHT, ColorSchemeSingleColorQuery.LIGHT});
        this.decorationPainter = new FlatDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();
        this.borderPainter = new CompositeBorderPainter("Graphite", new ClassicBorderPainter(), new DelegateBorderPainter("Graphite Inner", new ClassicBorderPainter(), -1593835521, 1627389951, 1627389951, new ColorSchemeTransform(){

            @Override
            public SubstanceColorScheme transform(SubstanceColorScheme scheme) {
                return scheme.tint(0.25);
            }
        }));
        this.highlightBorderPainter = new ClassicBorderPainter();
        this.watermarkScheme = schemes.get("Graphite Watermark");
    }

}

