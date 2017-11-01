package mchorse.metamorph.coremod.transform;

import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mchorse.metamorph.coremod.MetamorphCoremod;
import net.minecraft.launchwrapper.IClassTransformer;

public class THurtSound implements IClassTransformer
{
    private static final String ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";
    private static final String[] GET_HURT_SOUND = new String[]{"getHurtSound", "func_184581_c"};
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (transformedName.equals(ENTITY_PLAYER))
        {
            return transformClass(basicClass, MetamorphCoremod.obfuscated);
        }
        return basicClass;
    }
    
    private byte[] transformClass(byte[] basicClass, boolean obfuscated)
    {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode visitor = new ClassNode();
        reader.accept(visitor, 0);
        
        for (MethodNode method : visitor.methods)
        {
            if (method.name.equals(GET_HURT_SOUND[MetamorphCoremod.obfuscated ? 1 : 0]))
            {
                InsnList insns = method.instructions;
                ListIterator<AbstractInsnNode> iterator = insns.iterator();
                
                while (iterator.hasNext())
                {
                    AbstractInsnNode insn = iterator.next();
                    
                    if (insn.getOpcode() == Opcodes.ARETURN)
                    {
                        InsnList hook = new InsnList();
                        // SoundEvent should be on the stack
                        // this (EntityPlayer)
                        hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        // DamageSource
                        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "mchorse/metamorph/entity/SoundHandler",
                                "deathSoundHook112",
                                "(Lnet/minecraft/util/SoundEvent;" +
                                "Lnet/minecraft/entity/EntityLivingBase;"  +
                                "Lnet/minecraft/util/DamageSource;)" +
                                "Lnet/minecraft/util/SoundEvent;",
                                false));
                        
                        insns.insertBefore(insn, hook);
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        visitor.accept(writer);
        byte[] newClass = writer.toByteArray();
        
        return newClass;
    }
}
