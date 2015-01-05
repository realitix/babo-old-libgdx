package com.baboviolent.game.particle.effect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.BrownianAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.utils.Array;

/*
 * {
    unique: {
        billboardBatch: {
            class: com.badlogic.gdx.graphics.g3d.particles.ResourceData$SaveData,
            data: {
                cfg: {
                    class: com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch$Config,
                    useGPU: true,
                    mode: ViewPoint
                }
            },
            indices: [
                0
            ]
        }
    },
    data: [
        
    ],
    assets: [
        {
            filename: "/home/realitix/git/baboviolent/android/assets/data/particles/smoke2.png",
            type: com.badlogic.gdx.graphics.Texture
        }
    ],
    resource: {
        class: com.badlogic.gdx.graphics.g3d.particles.ParticleEffect,
        controllers: [
            {
                name: "Billboard Controller",
                emitter: {
                    class: com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter,
                    minParticleCount: 0,
                    maxParticleCount: 500,
                    continous: false,
                    emission: {
                        active: true,
                        lowMin: 0,
                        lowMax: 0,
                        highMin: 1500,
                        highMax: 1500,
                        relative: false,
                        scaling: [
                            0.4489796,
                            1
                        ],
                        timeline: [
                            0,
                            1
                        ]
                    },
                    delay: {
                        active: false,
                        lowMin: 0,
                        lowMax: 0
                    },
                    duration: {
                        active: true,
                        lowMin: 200,
                        lowMax: 200
                    },
                    life: {
                        active: true,
                        lowMin: 0,
                        lowMax: 0,
                        highMin: 500,
                        highMax: 500,
                        relative: false,
                        scaling: [
                            1,
                            1
                        ],
                        timeline: [
                            0,
                            1
                        ]
                    },
                    lifeOffset: {
                        active: false,
                        lowMin: 0,
                        lowMax: 0,
                        highMin: 0,
                        highMax: 0,
                        relative: false,
                        scaling: [
                            1
                        ],
                        timeline: [
                            0
                        ]
                    }
                },
                influencers: [
                    {
                        class: com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer,
                        spawnShape: {
                            class: com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue,
                            active: true,
                            xOffsetValue: {
                                active: false,
                                lowMin: 0,
                                lowMax: 0
                            },
                            yOffsetValue: {
                                active: false,
                                lowMin: 0,
                                lowMax: 0
                            },
                            zOffsetValue: {
                                active: false,
                                lowMin: 0,
                                lowMax: 0
                            },
                            spawnWidthValue: {
                                active: true,
                                lowMin: 50,
                                lowMax: 50,
                                highMin: 1000,
                                highMax: 1000,
                                relative: false,
                                scaling: [
                                    0,
                                    1,
                                    1
                                ],
                                timeline: [
                                    0,
                                    0.5068493,
                                    1
                                ]
                            },
                            spawnHeightValue: {
                                active: true,
                                lowMin: 0,
                                lowMax: 0,
                                highMin: 5,
                                highMax: 5,
                                relative: false,
                                scaling: [
                                    0,
                                    1
                                ],
                                timeline: [
                                    0,
                                    1
                                ]
                            },
                            spawnDepthValue: {
                                active: true,
                                lowMin: 0,
                                lowMax: 0,
                                highMin: 5,
                                highMax: 5,
                                relative: false,
                                scaling: [
                                    0,
                                    1
                                ],
                                timeline: [
                                    0,
                                    1
                                ]
                            },
                            edges: false
                        }
                    },
                    {
                        class: com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer$Single,
                        alpha: {
                            active: true,
                            lowMin: 0,
                            lowMax: 0,
                            highMin: 1,
                            highMax: 1,
                            relative: false,
                            scaling: [
                                1,
                                0
                            ],
                            timeline: [
                                0,
                                0.9884868
                            ]
                        },
                        color: {
                            active: false,
                            colors: [
                                0.56078434,
                                0.69411767,
                                0.8784314,
                                0,
                                0,
                                0
                            ],
                            timeline: [
                                0,
                                1
                            ]
                        }
                    },
                    {
                        class: com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer,
                        velocities: [
                            
                        ]
                    },
                    {
                        class: com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer,
                        value: {
                            active: false,
                            lowMin: 0,
                            lowMax: 0,
                            highMin: 30,
                            highMax: 30,
                            relative: true,
                            scaling: [
                                0,
                                1
                            ],
                            timeline: [
                                0,
                                1
                            ]
                        }
                    }
                ],
                renderer: {
                    class: com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer
                }
            }
        ]
    }
}
 */
public class Smoke1Effect extends BaboParticleEffect {
	
	public Smoke1Effect(BillboardParticleBatch batch, Texture texture) {
		super(batch, texture);
	}
	
	protected void configure() {
		//Emitter
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(0);
		emitter.setMaxParticleCount(500);
		emitter.setContinuous(false);

		emitter.getEmission().setActive(true);
		emitter.getEmission().setLow(0);
		emitter.getEmission().setHigh(1500);
		emitter.getEmission().setScaling(new float[] {0.5f, 1});
		emitter.getEmission().setTimeline(new float[] {0, 1});
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(200);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setLow(0);
		emitter.getLife().setHigh(500);
		emitter.getLife().setScaling(new float[] {1, 1});
		emitter.getLife().setTimeline(new float[] {0, 1});

		//Spawn
		LineSpawnShapeValue spawn = new LineSpawnShapeValue();
		spawn.setActive(true);
		spawn.xOffsetValue.setActive(false);
		spawn.yOffsetValue.setActive(false);
		spawn.zOffsetValue.setActive(false);
		
		spawn.getSpawnWidth().setActive(true);
		spawn.getSpawnWidth().setLow(50);
		spawn.getSpawnWidth().setHigh(1000);
		spawn.getSpawnWidth().setScaling(new float[] {0, 1, 1});
		spawn.getSpawnWidth().setTimeline(new float[] {0, 0.5f, 1});
		
		spawn.getSpawnHeight().setActive(true);
		spawn.getSpawnWidth().setLow(0);
		spawn.getSpawnWidth().setHigh(5);
		spawn.getSpawnWidth().setScaling(new float[] {0, 1});
		spawn.getSpawnWidth().setTimeline(new float[] {0, 1});
		
		PointSpawnShapeValue pointSpawnShapeValue = new PointSpawnShapeValue();		
		pointSpawnShapeValue.xOffsetValue.setLow(0, 1f);
		pointSpawnShapeValue.xOffsetValue.setActive(true);
		pointSpawnShapeValue.yOffsetValue.setLow(0, 1f);
		pointSpawnShapeValue.yOffsetValue.setActive(true);
		pointSpawnShapeValue.zOffsetValue.setLow(0, 1f);
		pointSpawnShapeValue.zOffsetValue.setActive(true);
		SpawnInfluencer spawnSource = new SpawnInfluencer(pointSpawnShapeValue);

		//Scale
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setTimeline(new float[]{0, 1});
		scaleInfluencer.value.setScaling(new float[]{1, 0});
		scaleInfluencer.value.setLow(0);
		scaleInfluencer.value.setHigh(1);

		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.colorValue.setColors(new float[] {colors[0], colors[1], colors[2], 0,0,0});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 0.5f, 0.8f, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {0, 0.15f, 0.5f, 0});
		
		//Dynamics
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		BrownianAcceleration modifier = new BrownianAcceleration();
		modifier.strengthValue.setTimeline(new float[]{0,1});
		modifier.strengthValue.setScaling(new float[]{0,1});
		modifier.strengthValue.setHigh(80);
		modifier.strengthValue.setLow(1, 5);
		dynamicsInfluencer.velocities.add(modifier);
		
		baboControllers.add(new ParticleController("Smoke1", emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(texture),
			spawnSource,
			scaleInfluencer,
			colorInfluencer,
			dynamicsInfluencer
			));
				
		super.configure();
	}
}
