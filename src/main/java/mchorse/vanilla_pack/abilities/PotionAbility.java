package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IntegerCache;

/**
 * Abstract potion ability
 * 
 * This is class is responsible for adding specific given potion effect from 
 * subclasses. You can also give the duration for the potion effect. 
 */
public abstract class PotionAbility extends Ability
{
    protected Potion potion;
    protected int duration = Integer.MAX_VALUE;

    @Override
    public void update(EntityLivingBase target)
    {
        if (!target.isPotionActive(this.potion))
        {
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(EntityLivingBase target)
    {
        target.addPotionEffect(new PotionEffect(this.potion, this.duration, 0, false, false));
    }

    @Override
    public void onDemorph(EntityLivingBase target)
    {
        target.removePotionEffect(this.potion);
    }
}