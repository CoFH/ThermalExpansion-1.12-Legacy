package cofh.thermalexpansion.gui.element;

import cofh.lib.gui.element.ElementListBox;
import cofh.lib.gui.element.listbox.ListBoxElementText;
import cofh.lib.transport.IEnderChannelRegistry.Frequency;
import cofh.thermalfoundation.fluid.TFFluids;

import net.minecraftforge.fluids.FluidStack;

public class ListBoxElementEnderText extends ListBoxElementText {

	private final Frequency freq;

	public ListBoxElementEnderText(Frequency freq) {

		super(freq.name);
		this.freq = freq;
	}

	@Override
	public Object getValue() {

		return freq;
	}

	@Override
	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor) {

		if (backColor == 1) {
			listBox.getContainerScreen().drawFluid(x, y, new FluidStack(TFFluids.fluidEnder, 1000), Math.max(getWidth(), listBox.getContentWidth()),
					getHeight());
		}
		super.draw(listBox, x, y, backColor, textColor);
	}

}
