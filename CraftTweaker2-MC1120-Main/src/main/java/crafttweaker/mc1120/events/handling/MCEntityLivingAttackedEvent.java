package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingAttackedEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class MCEntityLivingAttackedEvent implements EntityLivingAttackedEvent {
    
    private final LivingAttackEvent event;
    
    public MCEntityLivingAttackedEvent(LivingAttackEvent event) {
        this.event = event;
    }
    
    @Override
    public IDamageSource getDamageSource() {
        return CraftTweakerMC.getIDamageSource(event.getSource());
    }
    
    @Override
    public float getAmount() {
        return event.getAmount();
    }
    
    @Override
    public boolean isCanceled() {
        return event.isCanceled();
    }
    
    @Override
    public void setCanceled(boolean canceled) {
        event.setCanceled(canceled);
    }
    
    @Override
    public IEntityLivingBase getEntityLivingBase() {
        return CraftTweakerMC.getIEntityLivingBase(event.getEntityLiving());
    }
}
