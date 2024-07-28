/**
 * 
 */
package com.omo.free.simple.fx.tools;

import java.util.List;
import java.util.stream.Collectors;

import com.sun.javafx.scene.control.skin.ColorPickerSkin;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * {@code CustomColorPicker} is used to create a {@link ColorPicker} that contains only the Color Palette. The "Custom Color" option of the parent class {@code ColorPicker} has been removed.
 * 
 * @author <strong>Brandon Turner</strong> JCCC, bat000is - ITSD Nov 21, 2023
 */
public class CustomColorPicker extends ColorPicker {

    /**
     * Creates a default ColorPicker instance with a selected color set to white.
     */
    public CustomColorPicker() {
        super();
    }// end constructor

    /**
     * Creates a CustomColorPicker instance and sets the selected color to the given color.
     * 
     * @param color
     *        The Color to be set as the currently selected color of the {@code CustomColorPicker}.
     */
    public CustomColorPicker(Color color) {
        super(color);
    }// end constructor

    /**
     * Creates a CustomColorPicker instance and sets the selected color to the given color.
     * 
     * @param hexValue
     *        The Hex value of the Color to be set as the currently selected color of the {@code CustomColorPicker}.
     */
    public CustomColorPicker(String hexValue) {
        super();
        if(null != hexValue && !"".equals(hexValue)){
            this.setValue(Color.valueOf(hexValue));
        }// end if
    }// end constructor

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        this.getStylesheets().add("/com/omo/free/simple/fx/resources/colorPicker.css");
        return new CustomColorPickerSkin(this);
    }// end createDefaultSkin

    /**
     * Inner class called by the createDeaultSkin() on instantiation.
     */
    private class CustomColorPickerSkin extends ColorPickerSkin {

        /**
         * Constructor.
         * 
         * @param control
         *        The {@code ColorPicker} to pass to the super class constructor.
         */
        public CustomColorPickerSkin(ColorPicker control) {
            super(control);
        }// end constructor

        /**
         * {@inheritDoc}
         */
        @Override
        protected Node getPopupContent() {
            Node colorPalette = super.getPopupContent(); // This is an instance of private API ColorPalette which extends Region
            Region r = (Region) colorPalette;
            List<Node> vboxes = r.getChildrenUnmodifiable().stream().filter(e -> {
                return e instanceof VBox;
            }).collect(Collectors.toList()); // This ColorPalette contains a VBox which contains the Hyperlink and Separator we want to remove.
            for(Node n : vboxes){
                VBox vbox = (VBox) n;
                List<Node> hyperlinks = vbox.getChildren().stream().filter(e -> {
                    return e instanceof Hyperlink;
                }).collect(Collectors.toList());
                List<Node> separators = vbox.getChildren().stream().filter(e -> {
                    return e instanceof Separator;
                }).collect(Collectors.toList());
                vbox.getChildren().removeAll(hyperlinks); // Remove the hyperlinks
                vbox.getChildren().removeAll(separators); // Remove the separators
            }// end for
            return colorPalette;
        }// end getPopupContent

    }// end class

    /**
     * This method converts the selected {@link Color} to a Hex value.
     * <p>
     * Example: Gray is returned as #808080FF
     * </p>
     * 
     * @return The Hex value of the selected Color.
     */
    public String getColorAsHexValue() {
        Color color = this.getValue();
        int r = ((int) Math.round(color.getRed() * 255)) << 24;
        int g = ((int) Math.round(color.getGreen() * 255)) << 16;
        int b = ((int) Math.round(color.getBlue() * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }// end getColorHexValue

}// end class
