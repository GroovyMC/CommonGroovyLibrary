/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection GrPackage

import com.matyrobbrt.enhancedgroovy.dsl.ClassTransformer

final transformer = ((ClassTransformer) this.transformer)

transformer.addMethod([
        'name'      : 'encode',
        'modifiers' : ['public'],
        'parameters': [
                'buffer': 'net.minecraft.network.FriendlyByteBuf'
        ]
])
transformer.addMethod([
        'name'      : 'decode',
        'modifiers' : ['public', 'static'],
        'returnType': transformer.className,
        'parameters': [
                'buffer': 'net.minecraft.network.FriendlyByteBuf'
        ]
])