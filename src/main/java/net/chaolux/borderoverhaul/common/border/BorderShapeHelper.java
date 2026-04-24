package net.chaolux.borderoverhaul.common.border;

import net.chaolux.borderoverhaul.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class BorderShapeHelper {
    public static boolean isOutside(double x, double z, double halfBorder) {
        return switch (Config.BORDER_SHAPE.get()) {
            case SQUARE -> Math.abs(x) > halfBorder || Math.abs(z) > halfBorder;
            case CIRCLE -> (x*x+z*z) > (halfBorder*halfBorder);
            case OVAL -> {
                double rx=halfBorder;
                double rz=rx*Config.OVAL_Z_RATIO.get();
                yield (x*x) / (rx*rx) + (z*z) / (rz*rz) > 1.0;
            }
        };
    }

    public static double distanceOutside(double x, double z, double halfBorder) {
        return switch (Config.BORDER_SHAPE.get()) {
            case SQUARE -> Math.max(Math.max(Math.abs(x) - halfBorder, Math.abs(z) - halfBorder),0.0);
            case CIRCLE -> Math.max(Math.sqrt(x*x+z*z) - halfBorder,0.0);
            case OVAL -> {
                double rx=halfBorder;
                double rz=rx*Config.OVAL_Z_RATIO.get();
                double scale=Math.sqrt((x*x) / (rx*rx) + (z*z) / (rz*rz));
                if(scale <= 1.0) yield 0.0;
                double sqrt=Math.sqrt(x*x+z*z);
                double px=x / scale;
                double pz=z / scale;
                double psqrt=Math.sqrt(px*px+pz*pz);
                yield Math.max(sqrt - psqrt, 0.0);
            }
        };
    }

    public static Vec3 getOutside(double x, double z, double halfBorder, double safeOffset) {
        return switch (Config.BORDER_SHAPE.get()) {
            case SQUARE -> {
                double min= -halfBorder + safeOffset;
                double max=halfBorder - safeOffset;
                double tx=Mth.clamp(x,min,max);
                double tz=Mth.clamp(z,min,max);
                if(x >= halfBorder) {
                    tx=min;
                } else if(x <= -halfBorder) {
                    tx=max;
                }
                if(z >= halfBorder) {
                    tz=min;
                } else if(z <= -halfBorder) {
                    tz=max;
                }
                yield new Vec3(tx,0.0,tz);
            }
            case CIRCLE -> {
                double sqrt=Math.sqrt(x*x+z*z);
                if(sqrt == 0) sqrt=0.0001;
                double radius=halfBorder - safeOffset;
                double tx= -(x / sqrt)*radius;
                double tz= -(z / sqrt)*radius;
                yield new Vec3(tx,0.0,tz);
            }
            case OVAL -> {
                double rx=halfBorder - safeOffset;
                double rz= (halfBorder*Config.OVAL_Z_RATIO.get()) - safeOffset;
                double angle=Math.atan2(z / Math.max(rz,0.0001),x / Math.max(rx,0.0001));
                double tx= -Math.cos(angle) * rx;
                double tz= -Math.sin(angle) * rz;
                yield new Vec3(tx,0.0,tz);
            }
        };
    }

    public static BlockPos findSafeTeleportPos(ServerLevel serverLevel, double targetX, double targetZ, int findRadius) {
        int baseX=Mth.floor(targetX);
        int baseZ=Mth.floor(targetZ);
        for(int radius=0;
            radius <= findRadius;
            radius++) {
            for (int dx= -radius;
            dx <= radius;
            dx++) {
                for (int dz= -radius;
                dz <= radius;
                dz++) {
                    int x=baseX+dx;
                    int z=baseZ+dz;
                    int y=serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,x,z);
                    for(int checkY=Math.max(serverLevel.getMinBuildHeight() + 1, y - 3);
                    checkY <= Math.min(serverLevel.getMaxBuildHeight() - 2, y + 3);
                    checkY++) {
                        BlockPos pos=new BlockPos(x,checkY,z);
                        if(isSafePos(serverLevel,pos)) return pos;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSafePos(ServerLevel serverLevel, BlockPos pos) {
        BlockPos below=pos.below();
        BlockPos above=pos.above();
        BlockState belowState=serverLevel.getBlockState(below);
        BlockState posState=serverLevel.getBlockState(pos);
        BlockState aboveState=serverLevel.getBlockState(above);
        boolean solidBelow=belowState.blocksMotion();
        boolean posFree=posState.getCollisionShape(serverLevel,pos).isEmpty() && serverLevel.getFluidState(pos).isEmpty();
        boolean aboveFree=aboveState.getCollisionShape(serverLevel,above).isEmpty() && serverLevel.getFluidState(above).isEmpty();
        return solidBelow && posFree && aboveFree;

    }
}
