# 07 — Banco de Dados de Exercícios

Esta lista contém TODOS os exercícios disponíveis no app PeitoInfinity.
O agente LLM deve utilizar EXCLUSIVAMENTE exercícios desta lista ao gerar planos de treino.
Cada exercício possui um `id` único, nome, grupo muscular principal, grupos musculares secundários, tipo de equipamento e tipo de exercício.

## Formato dos Dados

Cada exercício será armazenado no banco de dados Room com a seguinte estrutura:

| Campo | Tipo | Descrição |
|-------|------|----------|
| id | String | Identificador único (ex: `chest_bench_press_flat`) |
| name | String | Nome do exercício em português |
| primaryMuscleGroup | MuscleGroup | Grupo muscular principal |
| secondaryMuscleGroups | List\<MuscleGroup\> | Grupos musculares secundários |
| equipment | Equipment | Equipamento necessário |
| exerciseType | ExerciseType | Tipo (COMPOUND, ISOLATION, CARDIO) |
| description | String | Descrição breve do movimento |
| difficulty | Difficulty | Nível de dificuldade |

### Enums

**MuscleGroup:** CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, FOREARMS, ABS, OBLIQUES, LOWER_BACK, QUADRICEPS, HAMSTRINGS, GLUTES, CALVES, HIP_FLEXORS, ADDUCTORS, ABDUCTORS, TRAPS, FULL_BODY, CARDIO_SYSTEM

**Equipment:** BARBELL, DUMBBELL, CABLE, MACHINE, SMITH_MACHINE, BODYWEIGHT, KETTLEBELL, RESISTANCE_BAND, EZ_BAR, TRAP_BAR, BENCH, PULL_UP_BAR, DIP_BARS, TREADMILL, STATIONARY_BIKE, ELLIPTICAL, ROWING_MACHINE, BATTLE_ROPES, MEDICINE_BALL, SWISS_BALL, TRX, NONE

**ExerciseType:** COMPOUND, ISOLATION, CARDIO, FLEXIBILITY

**Difficulty:** BEGINNER, INTERMEDIATE, ADVANCED

---

## PEITO (CHEST)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| chest_bench_press_flat_barbell | Supino Reto com Barra | CHEST | TRICEPS, SHOULDERS | BARBELL | COMPOUND | BEGINNER |
| chest_bench_press_flat_dumbbell | Supino Reto com Halteres | CHEST | TRICEPS, SHOULDERS | DUMBBELL | COMPOUND | BEGINNER |
| chest_bench_press_incline_barbell | Supino Inclinado com Barra | CHEST | TRICEPS, SHOULDERS | BARBELL | COMPOUND | BEGINNER |
| chest_bench_press_incline_dumbbell | Supino Inclinado com Halteres | CHEST | TRICEPS, SHOULDERS | DUMBBELL | COMPOUND | BEGINNER |
| chest_bench_press_decline_barbell | Supino Declinado com Barra | CHEST | TRICEPS, SHOULDERS | BARBELL | COMPOUND | INTERMEDIATE |
| chest_bench_press_decline_dumbbell | Supino Declinado com Halteres | CHEST | TRICEPS, SHOULDERS | DUMBBELL | COMPOUND | INTERMEDIATE |
| chest_fly_flat_dumbbell | Crucifixo Reto com Halteres | CHEST | SHOULDERS | DUMBBELL | ISOLATION | BEGINNER |
| chest_fly_incline_dumbbell | Crucifixo Inclinado com Halteres | CHEST | SHOULDERS | DUMBBELL | ISOLATION | BEGINNER |
| chest_fly_decline_dumbbell | Crucifixo Declinado com Halteres | CHEST | SHOULDERS | DUMBBELL | ISOLATION | INTERMEDIATE |
| chest_crossover_high | Crossover Alto | CHEST | SHOULDERS | CABLE | ISOLATION | INTERMEDIATE |
| chest_crossover_low | Crossover Baixo | CHEST | SHOULDERS | CABLE | ISOLATION | INTERMEDIATE |
| chest_crossover_mid | Crossover Meio | CHEST | SHOULDERS | CABLE | ISOLATION | INTERMEDIATE |
| chest_push_up | Flexão de Braço | CHEST | TRICEPS, SHOULDERS | BODYWEIGHT | COMPOUND | BEGINNER |
| chest_push_up_incline | Flexão de Braço Inclinada | CHEST | TRICEPS, SHOULDERS | BODYWEIGHT | COMPOUND | BEGINNER |
| chest_push_up_decline | Flexão de Braço Declinada | CHEST | TRICEPS, SHOULDERS | BODYWEIGHT | COMPOUND | INTERMEDIATE |
| chest_push_up_diamond | Flexão Diamante | CHEST | TRICEPS | BODYWEIGHT | COMPOUND | INTERMEDIATE |
| chest_peck_deck | Peck Deck | CHEST | SHOULDERS | MACHINE | ISOLATION | BEGINNER |
| chest_machine_press | Supino na Máquina | CHEST | TRICEPS, SHOULDERS | MACHINE | COMPOUND | BEGINNER |
| chest_pullover_dumbbell | Pullover com Haltere | CHEST | BACK, TRICEPS | DUMBBELL | COMPOUND | INTERMEDIATE |
| chest_chest_press_machine | Chest Press na Máquina | CHEST | TRICEPS, SHOULDERS | MACHINE | COMPOUND | BEGINNER |
| chest_bench_press_close_grip | Supino Pegada Fechada | CHEST | TRICEPS | BARBELL | COMPOUND | INTERMEDIATE |
| chest_svend_press | Svend Press | CHEST | SHOULDERS | DUMBBELL | ISOLATION | INTERMEDIATE |
| chest_bench_press_smith | Supino no Smith | CHEST | TRICEPS, SHOULDERS | SMITH_MACHINE | COMPOUND | BEGINNER |
| chest_push_up_wide | Flexão com Pegada Aberta | CHEST | SHOULDERS | BODYWEIGHT | COMPOUND | BEGINNER |

---

## COSTAS (BACK)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| back_lat_pulldown_wide | Puxada Frontal Aberta | BACK | BICEPS, FOREARMS | CABLE | COMPOUND | BEGINNER |
| back_lat_pulldown_close | Puxada Frontal Fechada | BACK | BICEPS, FOREARMS | CABLE | COMPOUND | BEGINNER |
| back_lat_pulldown_behind | Puxada Atrás da Nuca | BACK | BICEPS, SHOULDERS | CABLE | COMPOUND | INTERMEDIATE |
| back_bent_over_row_barbell | Remada Curvada com Barra | BACK | BICEPS, LOWER_BACK | BARBELL | COMPOUND | INTERMEDIATE |
| back_one_arm_row_dumbbell | Remada Unilateral com Haltere | BACK | BICEPS, FOREARMS | DUMBBELL | COMPOUND | BEGINNER |
| back_seated_cable_row | Remada Baixa no Cabo | BACK | BICEPS, FOREARMS | CABLE | COMPOUND | BEGINNER |
| back_t_bar_row | Remada Cavaleiro (T-Bar) | BACK | BICEPS, LOWER_BACK | BARBELL | COMPOUND | INTERMEDIATE |
| back_upright_row | Remada Alta | BACK | SHOULDERS, TRAPS | BARBELL | COMPOUND | INTERMEDIATE |
| back_pull_up | Barra Fixa (Pronada) | BACK | BICEPS, FOREARMS | PULL_UP_BAR | COMPOUND | INTERMEDIATE |
| back_chin_up | Barra Fixa Supinada | BACK | BICEPS, FOREARMS | PULL_UP_BAR | COMPOUND | INTERMEDIATE |
| back_pullover_machine | Pullover na Máquina | BACK | CHEST | MACHINE | ISOLATION | BEGINNER |
| back_machine_row | Remada na Máquina | BACK | BICEPS | MACHINE | COMPOUND | BEGINNER |
| back_deadlift | Levantamento Terra | BACK | HAMSTRINGS, GLUTES, LOWER_BACK | BARBELL | COMPOUND | ADVANCED |
| back_t_bar_row_machine | Remada T na Máquina | BACK | BICEPS | MACHINE | COMPOUND | BEGINNER |
| back_good_morning | Good Morning | BACK | HAMSTRINGS, LOWER_BACK | BARBELL | COMPOUND | INTERMEDIATE |
| back_hyperextension | Hiperextensão | BACK | LOWER_BACK, GLUTES | BODYWEIGHT | ISOLATION | BEGINNER |
| back_inverted_row | Remada Invertida | BACK | BICEPS, FOREARMS | BODYWEIGHT | COMPOUND | BEGINNER |
| back_shrug_barbell | Encolhimento com Barra | TRAPS | SHOULDERS | BARBELL | ISOLATION | BEGINNER |
| back_shrug_dumbbell | Encolhimento com Halteres | TRAPS | SHOULDERS | DUMBBELL | ISOLATION | BEGINNER |
| back_face_pull | Face Pull | BACK | SHOULDERS, TRAPS | CABLE | ISOLATION | BEGINNER |
| back_straight_arm_pulldown | Pulldown Braço Reto | BACK | TRICEPS | CABLE | ISOLATION | INTERMEDIATE |
| back_rack_pull | Rack Pull | BACK | LOWER_BACK, TRAPS | BARBELL | COMPOUND | ADVANCED |
| back_meadows_row | Remada Meadows | BACK | BICEPS | BARBELL | COMPOUND | ADVANCED |
| back_lat_pulldown_reverse | Puxada Frontal Supinada | BACK | BICEPS | CABLE | COMPOUND | BEGINNER |

---

## OMBROS (SHOULDERS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| shoulders_overhead_press_barbell | Desenvolvimento Militar com Barra | SHOULDERS | TRICEPS, TRAPS | BARBELL | COMPOUND | INTERMEDIATE |
| shoulders_overhead_press_dumbbell | Desenvolvimento com Halteres | SHOULDERS | TRICEPS, TRAPS | DUMBBELL | COMPOUND | BEGINNER |
| shoulders_lateral_raise_dumbbell | Elevação Lateral com Halteres | SHOULDERS | TRAPS | DUMBBELL | ISOLATION | BEGINNER |
| shoulders_front_raise_dumbbell | Elevação Frontal com Halteres | SHOULDERS | CHEST | DUMBBELL | ISOLATION | BEGINNER |
| shoulders_reverse_fly_dumbbell | Crucifixo Inverso com Halteres | SHOULDERS | BACK, TRAPS | DUMBBELL | ISOLATION | BEGINNER |
| shoulders_arnold_press | Arnold Press | SHOULDERS | TRICEPS, TRAPS | DUMBBELL | COMPOUND | INTERMEDIATE |
| shoulders_machine_press | Desenvolvimento na Máquina | SHOULDERS | TRICEPS | MACHINE | COMPOUND | BEGINNER |
| shoulders_lateral_raise_cable | Elevação Lateral no Cabo | SHOULDERS | TRAPS | CABLE | ISOLATION | BEGINNER |
| shoulders_lateral_raise_machine | Elevação Lateral na Máquina | SHOULDERS | TRAPS | MACHINE | ISOLATION | BEGINNER |
| shoulders_face_pull | Face Pull | SHOULDERS | BACK, TRAPS | CABLE | ISOLATION | BEGINNER |
| shoulders_smith_press | Desenvolvimento no Smith | SHOULDERS | TRICEPS | SMITH_MACHINE | COMPOUND | BEGINNER |
| shoulders_alternating_press | Desenvolvimento Alternado | SHOULDERS | TRICEPS | DUMBBELL | COMPOUND | INTERMEDIATE |
| shoulders_front_raise_barbell | Elevação Frontal com Barra | SHOULDERS | CHEST | BARBELL | ISOLATION | BEGINNER |
| shoulders_y_raise | Elevação em Y | SHOULDERS | TRAPS, BACK | DUMBBELL | ISOLATION | INTERMEDIATE |
| shoulders_upright_row_barbell | Remada Alta com Barra | SHOULDERS | TRAPS, BICEPS | BARBELL | COMPOUND | INTERMEDIATE |
| shoulders_upright_row_dumbbell | Remada Alta com Halteres | SHOULDERS | TRAPS, BICEPS | DUMBBELL | COMPOUND | INTERMEDIATE |
| shoulders_rear_delt_machine | Crucifixo Inverso na Máquina | SHOULDERS | BACK | MACHINE | ISOLATION | BEGINNER |
| shoulders_overhead_press_kettlebell | Desenvolvimento com Kettlebell | SHOULDERS | TRICEPS | KETTLEBELL | COMPOUND | INTERMEDIATE |
| shoulders_landmine_press | Landmine Press | SHOULDERS | CHEST, TRICEPS | BARBELL | COMPOUND | INTERMEDIATE |
| shoulders_front_raise_cable | Elevação Frontal no Cabo | SHOULDERS | CHEST | CABLE | ISOLATION | BEGINNER |

---

## BÍCEPS (BICEPS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| biceps_barbell_curl | Rosca Direta com Barra | BICEPS | FOREARMS | BARBELL | ISOLATION | BEGINNER |
| biceps_alternating_curl | Rosca Alternada com Halteres | BICEPS | FOREARMS | DUMBBELL | ISOLATION | BEGINNER |
| biceps_hammer_curl | Rosca Martelo | BICEPS | FOREARMS | DUMBBELL | ISOLATION | BEGINNER |
| biceps_concentration_curl | Rosca Concentrada | BICEPS | — | DUMBBELL | ISOLATION | BEGINNER |
| biceps_preacher_curl | Rosca Scott (Preacher) | BICEPS | FOREARMS | EZ_BAR | ISOLATION | BEGINNER |
| biceps_cable_curl | Rosca no Cabo | BICEPS | FOREARMS | CABLE | ISOLATION | BEGINNER |
| biceps_incline_curl | Rosca Inclinada | BICEPS | — | DUMBBELL | ISOLATION | INTERMEDIATE |
| biceps_21s_curl | Rosca 21 | BICEPS | FOREARMS | EZ_BAR | ISOLATION | INTERMEDIATE |
| biceps_reverse_curl | Rosca Inversa | BICEPS | FOREARMS | BARBELL | ISOLATION | INTERMEDIATE |
| biceps_spider_curl | Rosca Spider | BICEPS | — | DUMBBELL | ISOLATION | INTERMEDIATE |
| biceps_ez_bar_curl | Rosca com Barra W | BICEPS | FOREARMS | EZ_BAR | ISOLATION | BEGINNER |
| biceps_rope_curl | Rosca com Corda no Cabo | BICEPS | FOREARMS | CABLE | ISOLATION | BEGINNER |
| biceps_drag_curl | Rosca Drag Curl | BICEPS | FOREARMS | BARBELL | ISOLATION | INTERMEDIATE |
| biceps_cross_body_curl | Rosca Cross Body | BICEPS | FOREARMS | DUMBBELL | ISOLATION | BEGINNER |

---

## TRÍCEPS (TRICEPS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| triceps_pushdown_bar | Tríceps Pulley com Barra | TRICEPS | — | CABLE | ISOLATION | BEGINNER |
| triceps_skull_crusher | Tríceps Testa (Skull Crusher) | TRICEPS | — | EZ_BAR | ISOLATION | INTERMEDIATE |
| triceps_french_press | Tríceps Francês | TRICEPS | — | DUMBBELL | ISOLATION | INTERMEDIATE |
| triceps_rope_pushdown | Tríceps com Corda | TRICEPS | — | CABLE | ISOLATION | BEGINNER |
| triceps_kickback | Tríceps Coice com Haltere | TRICEPS | — | DUMBBELL | ISOLATION | BEGINNER |
| triceps_bench_dip | Tríceps no Banco | TRICEPS | CHEST, SHOULDERS | BODYWEIGHT | COMPOUND | BEGINNER |
| triceps_dip | Mergulho em Paralelas | TRICEPS | CHEST, SHOULDERS | DIP_BARS | COMPOUND | INTERMEDIATE |
| triceps_machine | Tríceps na Máquina | TRICEPS | — | MACHINE | ISOLATION | BEGINNER |
| triceps_close_grip_bench | Supino Pegada Fechada | TRICEPS | CHEST, SHOULDERS | BARBELL | COMPOUND | INTERMEDIATE |
| triceps_single_arm_pushdown | Tríceps Unilateral no Cabo | TRICEPS | — | CABLE | ISOLATION | BEGINNER |
| triceps_overhead_extension | Tríceps Overhead com Haltere | TRICEPS | — | DUMBBELL | ISOLATION | INTERMEDIATE |
| triceps_overhead_cable | Tríceps Overhead no Cabo | TRICEPS | — | CABLE | ISOLATION | INTERMEDIATE |
| triceps_reverse_grip_pushdown | Tríceps Pulley Supinado | TRICEPS | — | CABLE | ISOLATION | INTERMEDIATE |
| triceps_diamond_push_up | Flexão Diamante | TRICEPS | CHEST | BODYWEIGHT | COMPOUND | INTERMEDIATE |

---

## QUADRÍCEPS (QUADRICEPS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| quads_barbell_squat | Agachamento Livre com Barra | QUADRICEPS | GLUTES, HAMSTRINGS | BARBELL | COMPOUND | INTERMEDIATE |
| quads_smith_squat | Agachamento no Smith | QUADRICEPS | GLUTES, HAMSTRINGS | SMITH_MACHINE | COMPOUND | BEGINNER |
| quads_leg_press_45 | Leg Press 45° | QUADRICEPS | GLUTES, HAMSTRINGS | MACHINE | COMPOUND | BEGINNER |
| quads_leg_press_horizontal | Leg Press Horizontal | QUADRICEPS | GLUTES | MACHINE | COMPOUND | BEGINNER |
| quads_leg_extension | Cadeira Extensora | QUADRICEPS | — | MACHINE | ISOLATION | BEGINNER |
| quads_hack_squat | Agachamento Hack | QUADRICEPS | GLUTES | MACHINE | COMPOUND | INTERMEDIATE |
| quads_front_squat | Agachamento Frontal | QUADRICEPS | GLUTES, ABS | BARBELL | COMPOUND | ADVANCED |
| quads_bulgarian_split_squat | Agachamento Búlgaro | QUADRICEPS | GLUTES, HAMSTRINGS | DUMBBELL | COMPOUND | INTERMEDIATE |
| quads_walking_lunge | Avanço Caminhando | QUADRICEPS | GLUTES, HAMSTRINGS | DUMBBELL | COMPOUND | INTERMEDIATE |
| quads_stationary_lunge | Avanço Estacionário | QUADRICEPS | GLUTES, HAMSTRINGS | DUMBBELL | COMPOUND | BEGINNER |
| quads_lateral_lunge | Passada Lateral | QUADRICEPS | ADDUCTORS, GLUTES | DUMBBELL | COMPOUND | INTERMEDIATE |
| quads_sumo_squat | Agachamento Sumô | QUADRICEPS | ADDUCTORS, GLUTES | DUMBBELL | COMPOUND | BEGINNER |
| quads_sissy_squat | Sissy Squat | QUADRICEPS | — | BODYWEIGHT | ISOLATION | ADVANCED |
| quads_step_up | Step Up | QUADRICEPS | GLUTES | DUMBBELL | COMPOUND | BEGINNER |
| quads_goblet_squat | Agachamento Goblet | QUADRICEPS | GLUTES, ABS | DUMBBELL | COMPOUND | BEGINNER |
| quads_belt_squat | Belt Squat | QUADRICEPS | GLUTES | MACHINE | COMPOUND | INTERMEDIATE |
| quads_pendulum_squat | Pendulum Squat | QUADRICEPS | GLUTES | MACHINE | COMPOUND | INTERMEDIATE |

---

## POSTERIOR DE COXA (HAMSTRINGS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| hamstrings_lying_leg_curl | Mesa Flexora (Deitado) | HAMSTRINGS | — | MACHINE | ISOLATION | BEGINNER |
| hamstrings_seated_leg_curl | Cadeira Flexora (Sentado) | HAMSTRINGS | — | MACHINE | ISOLATION | BEGINNER |
| hamstrings_stiff_barbell | Stiff com Barra | HAMSTRINGS | GLUTES, LOWER_BACK | BARBELL | COMPOUND | INTERMEDIATE |
| hamstrings_stiff_dumbbell | Stiff com Halteres | HAMSTRINGS | GLUTES, LOWER_BACK | DUMBBELL | COMPOUND | BEGINNER |
| hamstrings_single_leg_stiff | Stiff Unilateral | HAMSTRINGS | GLUTES, LOWER_BACK | DUMBBELL | COMPOUND | INTERMEDIATE |
| hamstrings_romanian_deadlift | Levantamento Terra Romeno | HAMSTRINGS | GLUTES, LOWER_BACK | BARBELL | COMPOUND | INTERMEDIATE |
| hamstrings_good_morning | Good Morning | HAMSTRINGS | LOWER_BACK, GLUTES | BARBELL | COMPOUND | INTERMEDIATE |
| hamstrings_nordic_curl | Flexão Nórdica | HAMSTRINGS | — | BODYWEIGHT | ISOLATION | ADVANCED |
| hamstrings_glute_ham_raise | Glute Ham Raise | HAMSTRINGS | GLUTES | BODYWEIGHT | COMPOUND | ADVANCED |
| hamstrings_cable_kickback | Kickback no Cabo para Posterior | HAMSTRINGS | GLUTES | CABLE | ISOLATION | BEGINNER |
| hamstrings_swiss_ball_curl | Leg Curl na Bola Suíça | HAMSTRINGS | GLUTES | SWISS_BALL | ISOLATION | INTERMEDIATE |

---

## GLÚTEOS (GLUTES)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| glutes_hip_thrust_barbell | Hip Thrust com Barra | GLUTES | HAMSTRINGS | BARBELL | COMPOUND | INTERMEDIATE |
| glutes_hip_thrust_machine | Hip Thrust na Máquina | GLUTES | HAMSTRINGS | MACHINE | COMPOUND | BEGINNER |
| glutes_glute_bridge | Elevação Pélvica (Ponte) | GLUTES | HAMSTRINGS | BODYWEIGHT | ISOLATION | BEGINNER |
| glutes_glute_bridge_barbell | Ponte de Glúteos com Barra | GLUTES | HAMSTRINGS | BARBELL | COMPOUND | INTERMEDIATE |
| glutes_hip_abduction_machine | Abdução de Quadril na Máquina | GLUTES | ABDUCTORS | MACHINE | ISOLATION | BEGINNER |
| glutes_hip_abduction_cable | Abdução de Quadril no Cabo | GLUTES | ABDUCTORS | CABLE | ISOLATION | BEGINNER |
| glutes_cable_kickback | Coice no Cabo (Glúteo) | GLUTES | HAMSTRINGS | CABLE | ISOLATION | BEGINNER |
| glutes_sumo_squat | Agachamento Sumô para Glúteos | GLUTES | ADDUCTORS, QUADRICEPS | DUMBBELL | COMPOUND | BEGINNER |
| glutes_lateral_band_walk | Caminhada Lateral com Elástico | GLUTES | ABDUCTORS | RESISTANCE_BAND | ISOLATION | BEGINNER |
| glutes_donkey_kick | Coice na Máquina | GLUTES | HAMSTRINGS | MACHINE | ISOLATION | BEGINNER |
| glutes_step_up_glute | Step Up com Foco em Glúteo | GLUTES | QUADRICEPS | DUMBBELL | COMPOUND | BEGINNER |
| glutes_single_leg_hip_thrust | Hip Thrust Unilateral | GLUTES | HAMSTRINGS | BODYWEIGHT | ISOLATION | INTERMEDIATE |

---

## PANTURRILHA (CALVES)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| calves_standing_calf_raise_machine | Panturrilha em Pé na Máquina | CALVES | — | MACHINE | ISOLATION | BEGINNER |
| calves_seated_calf_raise | Panturrilha Sentado | CALVES | — | MACHINE | ISOLATION | BEGINNER |
| calves_leg_press_calf_raise | Panturrilha no Leg Press | CALVES | — | MACHINE | ISOLATION | BEGINNER |
| calves_smith_calf_raise | Panturrilha no Smith | CALVES | — | SMITH_MACHINE | ISOLATION | BEGINNER |
| calves_single_leg_calf_raise | Panturrilha Unilateral | CALVES | — | BODYWEIGHT | ISOLATION | BEGINNER |
| calves_dumbbell_calf_raise | Panturrilha com Halteres | CALVES | — | DUMBBELL | ISOLATION | BEGINNER |
| calves_donkey_calf_raise | Panturrilha Donkey | CALVES | — | MACHINE | ISOLATION | INTERMEDIATE |

---

## ABDÔMEN (ABS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| abs_crunch | Abdominal Crunch | ABS | — | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_reverse_crunch | Abdominal Infra (Reverso) | ABS | HIP_FLEXORS | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_plank_front | Prancha Frontal | ABS | LOWER_BACK, SHOULDERS | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_plank_side | Prancha Lateral | ABS | OBLIQUES | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_bicycle_crunch | Abdominal Bicicleta | ABS | OBLIQUES | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_leg_raise_hanging | Elevação de Pernas (Suspenso) | ABS | HIP_FLEXORS | PULL_UP_BAR | ISOLATION | INTERMEDIATE |
| abs_leg_raise_lying | Elevação de Pernas (Deitado) | ABS | HIP_FLEXORS | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_machine_crunch | Abdominal na Máquina | ABS | — | MACHINE | ISOLATION | BEGINNER |
| abs_cable_crunch | Abdominal com Corda (Cable Crunch) | ABS | — | CABLE | ISOLATION | INTERMEDIATE |
| abs_oblique_crunch | Abdominal Oblíquo | ABS | OBLIQUES | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_russian_twist | Russian Twist | ABS | OBLIQUES | MEDICINE_BALL | ISOLATION | INTERMEDIATE |
| abs_mountain_climber | Mountain Climber | ABS | HIP_FLEXORS, SHOULDERS | BODYWEIGHT | COMPOUND | BEGINNER |
| abs_ab_wheel | Abdominal com Roda (Ab Wheel) | ABS | LOWER_BACK, SHOULDERS | NONE | ISOLATION | ADVANCED |
| abs_v_up | V-Up | ABS | HIP_FLEXORS | BODYWEIGHT | ISOLATION | INTERMEDIATE |
| abs_dragon_flag | Dragon Flag | ABS | LOWER_BACK | BODYWEIGHT | ISOLATION | ADVANCED |
| abs_woodchop_cable | Woodchop no Cabo | ABS | OBLIQUES | CABLE | COMPOUND | INTERMEDIATE |
| abs_dead_bug | Dead Bug | ABS | LOWER_BACK | BODYWEIGHT | ISOLATION | BEGINNER |
| abs_hollow_hold | Hollow Hold | ABS | HIP_FLEXORS | BODYWEIGHT | ISOLATION | INTERMEDIATE |

---

## ANTEBRAÇO (FOREARMS)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| forearms_wrist_curl | Rosca de Punho | FOREARMS | — | DUMBBELL | ISOLATION | BEGINNER |
| forearms_reverse_wrist_curl | Rosca de Punho Inversa | FOREARMS | — | DUMBBELL | ISOLATION | BEGINNER |
| forearms_farmer_walk | Farmer Walk | FOREARMS | TRAPS, ABS | DUMBBELL | COMPOUND | BEGINNER |
| forearms_wrist_roller | Wrist Roller | FOREARMS | — | NONE | ISOLATION | INTERMEDIATE |
| forearms_plate_pinch | Plate Pinch Hold | FOREARMS | — | NONE | ISOLATION | INTERMEDIATE |
| forearms_towel_hang | Suspensão na Toalha | FOREARMS | BACK | PULL_UP_BAR | ISOLATION | ADVANCED |

---

## CARDIO

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| cardio_treadmill_run | Corrida na Esteira | CARDIO_SYSTEM | QUADRICEPS, CALVES | TREADMILL | CARDIO | BEGINNER |
| cardio_treadmill_walk | Caminhada na Esteira | CARDIO_SYSTEM | QUADRICEPS | TREADMILL | CARDIO | BEGINNER |
| cardio_treadmill_incline_walk | Caminhada Inclinada na Esteira | CARDIO_SYSTEM | GLUTES, CALVES | TREADMILL | CARDIO | BEGINNER |
| cardio_stationary_bike | Bicicleta Ergométrica | CARDIO_SYSTEM | QUADRICEPS | STATIONARY_BIKE | CARDIO | BEGINNER |
| cardio_elliptical | Elíptico | CARDIO_SYSTEM | QUADRICEPS, GLUTES | ELLIPTICAL | CARDIO | BEGINNER |
| cardio_rowing_machine | Remo Ergômetro | CARDIO_SYSTEM | BACK, BICEPS | ROWING_MACHINE | CARDIO | BEGINNER |
| cardio_jump_rope | Pular Corda | CARDIO_SYSTEM | CALVES, SHOULDERS | NONE | CARDIO | INTERMEDIATE |
| cardio_hiit_treadmill | HIIT na Esteira | CARDIO_SYSTEM | QUADRICEPS, CALVES | TREADMILL | CARDIO | INTERMEDIATE |
| cardio_stairmaster | Escada (Stairmaster) | CARDIO_SYSTEM | QUADRICEPS, GLUTES | MACHINE | CARDIO | INTERMEDIATE |
| cardio_spinning | Spinning (Bike Indoor) | CARDIO_SYSTEM | QUADRICEPS, CALVES | STATIONARY_BIKE | CARDIO | INTERMEDIATE |
| cardio_battle_ropes | Battle Ropes | CARDIO_SYSTEM | SHOULDERS, ABS | BATTLE_ROPES | CARDIO | INTERMEDIATE |
| cardio_burpees | Burpees | CARDIO_SYSTEM | FULL_BODY | BODYWEIGHT | CARDIO | INTERMEDIATE |

---

## FLEXIBILIDADE (FLEXIBILITY)

| ID | Nome | Grupo Principal | Grupos Secundários | Equipamento | Tipo | Dificuldade |
|----|------|----------------|-------------------|-------------|------|------------|
| flex_foam_rolling | Foam Rolling (Auto-liberação) | FULL_BODY | — | NONE | FLEXIBILITY | BEGINNER |
| flex_dynamic_stretch | Alongamento Dinâmico | FULL_BODY | — | NONE | FLEXIBILITY | BEGINNER |
| flex_static_stretch | Alongamento Estático | FULL_BODY | — | NONE | FLEXIBILITY | BEGINNER |

---

## Resumo

| Grupo Muscular | Quantidade de Exercícios |
|---|---|
| Peito (CHEST) | 24 |
| Costas (BACK) | 24 |
| Ombros (SHOULDERS) | 20 |
| Bíceps (BICEPS) | 14 |
| Tríceps (TRICEPS) | 14 |
| Quadríceps (QUADRICEPS) | 17 |
| Posterior de Coxa (HAMSTRINGS) | 11 |
| Glúteos (GLUTES) | 12 |
| Panturrilha (CALVES) | 7 |
| Abdômen (ABS) | 18 |
| Antebraço (FOREARMS) | 6 |
| Cardio (CARDIO_SYSTEM) | 12 |
| Flexibilidade (FULL_BODY) | 3 |

**Total de exercícios: 182**

---

## Notas para o Agente Codificador

1. Todos os exercícios desta lista devem ser pré-populados no banco de dados Room na primeira execução do app (usando `RoomDatabase.Callback` no `onCreate`).
2. O campo `id` segue o padrão: `{grupo}_{nome_exercicio}` usando snake_case e sem acentos.
3. A lista de exercícios é estática e NÃO deve ser editável pelo usuário.
4. O agente LLM recebe esta lista de IDs como contexto ao gerar planos de treino.
5. Na tela de "Exercícios Disponíveis", os exercícios devem ser agrupados por grupo muscular com busca/filtro.
6. Criar um object `ExerciseData` no package `data.local.database` com todos os 182 exercícios como uma lista estática de `ExerciseEntity`.
7. Os grupos secundários vazios (—) devem ser representados como lista vazia `[]` no JSON.
8. As cores de cada grupo muscular estão definidas em `spec/05-design-system.md`.
9. Os ícones de dificuldade sugeridos: BEGINNER = 🟢, INTERMEDIATE = 🟡, ADVANCED = 🔴.
