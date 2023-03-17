/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection unused

package io.github.groovymc.cgl.api.extension.math

import com.mojang.math.*
import groovy.transform.CompileStatic
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Arithmetic extensions for Minecraft vectors.
 * @author CommonGroovyLibrary
 */
@CompileStatic
class ArithmeticExtension {
    // region BlockPos
    static BlockPos plus(BlockPos self, Vec3i other) {
        return self.offset(other)
    }

    static BlockPos negative(BlockPos self) {
        return self * -1
    }

    static BlockPos div(BlockPos self, int scalar) {
        return new BlockPos(self.x.intdiv(scalar) as int, self.y.intdiv(scalar) as int, self.z.intdiv(scalar) as int)
    }

    static BlockPos minus(BlockPos self, Vec3i other) {
        return self.subtract(other)
    }

    static BlockPos multiply(int self, BlockPos pos) {
        return pos * self
    }

    static <T> T asType(BlockPos self, Class<T> type) {
        return switch (type) {
            case Vec3, Position ->
                (T) new Vec3(self.x, self.y, self.z)
            default ->
                (T) DefaultGroovyMethods.asType(self, type)
        }
    }
    // endregion BlockPos

    // region Vec3i
    static Vec3i plus(Vec3i self, Vec3i other) {
        return self.offset(other)
    }

    static Vec3i negative(Vec3i self) {
        return self * -1
    }

    static Vec3i div(Vec3i self, int scalar) {
        return new Vec3i(self.x.intdiv(scalar) as int, self.y.intdiv(scalar) as int, self.z.intdiv(scalar) as int)
    }

    static Vec3i minus(Vec3i self, Vec3i other) {
        return self.subtract(other)
    }

    static Vec3i multiply(int self, Vec3i pos) {
        return pos * self
    }

    static <T> T asType(Vec3i self, Class<T> type) {
        return switch (type) {
            case Vec3, Position ->
                (T) new Vec3(self.x, self.y, self.z)
            case BlockPos ->
                (T) new BlockPos(self.x, self.y, self.z)
            default ->
                (T) DefaultGroovyMethods.asType(self, type)
        }
    }
    // endregion Vec3

    // region Vec3/Position
    static Vec3 plus(Position self, Position other) {
        return new Vec3(self.x()+other.x(), self.y()+other.y(), self.z()+other.z())
    }

    static Vec3 multiply(Position self, double other) {
        return new Vec3(self.x()*other, self.y()*other, self.z()*other)
    }

    static Vec3 negative(Position self) {
        return multiply(self,-1)
    }

    static Vec3 div(Position self, double other) {
        return new Vec3(self.x()/other as double, self.y()/other as double, self.z()/other as double)
    }

    static Vec3 minus(Position self, Position other) {
        return new Vec3(self.x()-other.x(), self.y()-other.y(), self.z()-other.z())
    }

    static Vec3 multiply(double self, Position pos) {
        return multiply(pos,self)
    }
    // endregion

    // region Vec2
    static Vec2 plus(Vec2 self, Vec2 other) {
        return new Vec2(self.x+other.x as float, self.y+other.y as float)
    }

    static Vec2 multiply(Vec2 self, double other) {
        return new Vec2(self.x*other as float, self.y*other as float)
    }

    static Vec2 negative(Vec2 self) {
        return self.negated()
    }

    static Vec2 div(Vec2 self, double other) {
        return new Vec2(self.x/other as float, self.y/other as float)
    }

    static Vec2 minus(Vec2 self, Vec2 other) {
        return new Vec2(self.x-other.x as float, self.y-other.y as float)
    }

    static Vec2 multiply(double self, Vec2 pos) {
        return multiply(pos,self)
    }

    static <T> T asType(Vec2 self, Class<T> type) {
        return switch (type) {
            case Vec3, Position ->
                (T) new Vec3(self.x, self.y, 0)
            default ->
                (T) DefaultGroovyMethods.asType(self, type)
        }
    }
    // endregion

    // region AABB
    static AABB plus(AABB self, BlockPos other) {
        return self.move(other)
    }

    static AABB plus(AABB self, Vec3 other) {
        return self.move(other)
    }
    // endregion

    // region VoxelShapes
    static VoxelShape plus(VoxelShape self, VoxelShape other) {
        return Shapes.join(self, other, BooleanOp.OR)
    }

    static VoxelShape or(VoxelShape self, VoxelShape other) {
        return Shapes.join(self, other, BooleanOp.OR)
    }

    static VoxelShape and(VoxelShape self, VoxelShape other) {
        return Shapes.join(self, other, BooleanOp.AND)
    }

    static VoxelShape xor(VoxelShape self, VoxelShape other) {
        return Shapes.join(self, other, BooleanOp.NOT_SAME)
    }

    static VoxelShape minus(VoxelShape self, VoxelShape other) {
        return Shapes.join(self, other, BooleanOp.NOT_SECOND)
    }
    // endregion

    // region SymmetricGroup3
    static int getAt(SymmetricGroup3 self, int p) {
        return self.permutation(p)
    }

    static SymmetricGroup3 multiply(SymmetricGroup3 self, SymmetricGroup3 other) {
        return self.compose(other)
    }
    
    static SymmetricGroup3 negative(SymmetricGroup3 self) {
        return switch(self) {
            case SymmetricGroup3.P231 ->
                SymmetricGroup3.P312
            case SymmetricGroup3.P312 ->
                SymmetricGroup3.P231
            default ->
                self
        }
    }
    // endregion

    // region OctahedralGroup
    static OctahedralGroup multiply(OctahedralGroup self, OctahedralGroup other) {
        return self.compose(other)
    }

    static OctahedralGroup negative(OctahedralGroup self) {
        return self.inverse()
    }
    // endregion

    // region Transformation
    static Transformation multiply(Transformation self, Transformation other) {
        return self.compose(other)
    }

    static Transformation negative(Transformation self) {
        return self.inverse()
    }
    // endregion
}
