import random
import pandas as pd

# Tabla de indices
# Tipo envio: 0 = NORMAL, 1 = Express
# Saturacion: 0 = Baja, 1 = Media, 2 = Alta
# Prioridad: 0 = Baja, 1 = Media, 2 = Alta

def generar_distancia():
    r = random.random()

    if r < 0.7:
        # Cortas (1–800 km)
        return int(random.triangular(1, 800, 100))
    elif r < 0.9:
        # Medias (800–2000 km)
        return int(random.triangular(800, 2000, 1200))
    else:
        # Largas (2000–4400 km)
        return int(random.triangular(2000, 4400, 3000))

def generar_ventana(distancia, express):
    # tiempo base mínimo en horas
    # asumimos velocidad promedio de 70 km/h
    tiempo_base = distancia / 70

    # express = más exigente
    if express:
        tiempo_base *= 0.75

    # agregamos margen logístico
    tiempo_base *= random.uniform(1.1, 1.5)

    # redondeamos a valores típicos
    if tiempo_base <= 6:
        return 4
    elif tiempo_base <= 12:
        return 8
    elif tiempo_base <= 24:
        return 24
    elif tiempo_base <= 48:
        return 48
    else:
        return 72

def generar_dato():
    distancia = generar_distancia()
    express = random.choices([0, 1], weights=[0.7, 0.3])[0]
    ventana = generar_ventana(distancia, express)
    volumen = int(random.triangular(1, 50, 10))
    fragil = random.choices([0, 1], weights=[0.7, 0.3])[0]
    frio = random.choices([0, 1], weights=[0.85, 0.15])[0]
    saturacion = random.choices([0, 1, 2], weights=[0.5, 0.3, 0.2])[0]

    # Sistema de scoring (coherente con dominio)
    score = 0

    if distancia > 800:
        score += 1
    if distancia > 2000:
        score += 2

    if express:
        score += 3

    if ventana <= 4:
        score += 2

    if volumen > 20:
        score += 1

    if fragil:
        score += 1
    if frio:
        score += 1

    if saturacion == 1:
        score += 1
    elif saturacion == 2:
        score += 3

    # Clasificación final
    if score >= 7:
        prioridad = 2
    elif score >= 4:
        prioridad = 1
    else:
        prioridad = 0

    return [
        distancia, express, ventana, volumen,
        fragil, frio, saturacion, prioridad
    ]

def generar_dataset(n=500):
    data = [generar_dato() for _ in range(n)]

    data_frame = pd.DataFrame(data, columns=[
        "distancia_km",
        "tipo_envio",
        "ventana_horas",
        "volumen",
        "fragil",
        "frio",
        "saturacion",
        "prioridad"
    ])

    return data_frame


if __name__ == "__main__":
    data_frame = generar_dataset(5000)
    data_frame.to_csv("dataset_envios.csv", index=False)
    print("Dataset generado")