package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * User: yevgenyk
 * Date: 07/01/14
 */
public interface LibGdxConsoleWidgetFactory {
    Label createBufferEntryLabel(String text);

    Drawable createTerminalBufferBackground();
    Drawable createConsoleBottomRowBackground();
    Drawable createCurrentPathLabelBackground();

    Label createCurrentPathLabel(String currentPath);
    TextField createInputTextField(String initialText);
    Button createCloseButton();
}
