package cofh.thermalexpansion.gui.element;

import cofh.lib.gui.element.ElementListBox;
import cofh.lib.gui.element.listbox.ListBoxElementText;
import cofh.thermalfoundation.fluid.TFFluids;

import net.minecraftforge.fluids.FluidStack;


public class ListBoxElementEnderText extends ListBoxElementText {

	public final int freq;

	public ListBoxElementEnderText(String text, int freq) {

		super(text);
		this.freq = freq;
	}

	@Override
	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor) {

		if (backColor == 1) {
			listBox.getContainerScreen().drawFluid(x, y, new FluidStack(TFFluids.fluidEnder, 1000), getWidth(), getHeight());
		}
		super.draw(listBox, x, y, backColor, textColor);
	}

}
