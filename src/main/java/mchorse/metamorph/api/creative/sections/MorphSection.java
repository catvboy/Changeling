package mchorse.metamorph.api.creative.sections;

import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.creative.GuiMorphSection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MorphSection
{
	public String title;
	public List<MorphCategory> categories = new ArrayList<MorphCategory>();

	public MorphSection(String title)
	{
		this.title = title;
	}

	public void add(MorphCategory category)
	{
		this.categories.add(category);
	}

	public void remove(MorphCategory category)
	{
		this.categories.remove(category);
	}

	/**
	 * This method gets called when a new morph picker appears
	 */
	public void update(World world)
	{}

	/**
	 * This method gets called when player exits to the main menu
	 */
	public void reset()
	{}

	@SideOnly(Side.CLIENT)
	public GuiMorphSection getGUI(Minecraft mc, GuiCreativeMorphs parent, Consumer<GuiMorphSection> callback)
	{
		return new GuiMorphSection(mc, parent, this, callback);
	}
}