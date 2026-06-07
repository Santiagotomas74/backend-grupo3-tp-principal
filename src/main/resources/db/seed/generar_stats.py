import unicodedata

import numpy as np
import pandas as pd
import random
from datetime import datetime, timedelta

# ============================================================
# CONFIGURATION & CONSTANTS
# ============================================================
OUTPUT_PRODUCTION_CSV = "src\\main\\resources\\db\\seed\\transportistas_ml.csv"
CANTIDAD_CHOFERES = 100

np.random.seed(42)
random.seed(42)

PERSONALIDADES = {
    "Veterano": {"prob": 0.25, "exp_mu": 26, "exp_std": 5, "exp_min": 18, "exp_max": 38, "fiab_alpha": 18, "fiab_beta": 3, "larga_mu": 0.90, "larga_std": 0.05, "pesada_mu": 0.82, "pesada_std": 0.08, "impru_alpha": 3, "impru_beta": 12},
    "Joven_Ambicioso": {"prob": 0.20, "exp_mu": 4.5, "exp_std": 2, "exp_min": 1, "exp_max": 8, "fiab_alpha": 7, "fiab_beta": 4, "larga_mu": 0.87, "larga_std": 0.08, "pesada_mu": 0.77, "pesada_std": 0.10, "impru_alpha": 6, "impru_beta": 5},
    "Padre_Familia": {"prob": 0.18, "exp_mu": 20, "exp_std": 5, "exp_min": 12, "exp_max": 28, "fiab_alpha": 15, "fiab_beta": 3, "larga_mu": 0.52, "larga_std": 0.10, "pesada_mu": 0.62, "pesada_std": 0.12, "impru_alpha": 2.5, "impru_beta": 11},
    "Gaucho": {"prob": 0.12, "exp_mu": 17, "exp_std": 5, "exp_min": 10, "exp_max": 25, "fiab_alpha": 6, "fiab_beta": 5, "larga_mu": 0.94, "larga_std": 0.04, "pesada_mu": 0.75, "pesada_std": 0.09, "impru_alpha": 7, "impru_beta": 4.5},
    "Viejo_Grunyon": {"prob": 0.10, "exp_mu": 29, "exp_std": 5, "exp_min": 22, "exp_max": 38, "fiab_alpha": 9, "fiab_beta": 5, "larga_mu": 0.42, "larga_std": 0.10, "pesada_mu": 0.88, "pesada_std": 0.07, "impru_alpha": 5.5, "impru_beta": 6},
    "Profesional_Corpo": {"prob": 0.08, "exp_mu": 14, "exp_std": 4, "exp_min": 8, "exp_max": 20, "fiab_alpha": 16, "fiab_beta": 3, "larga_mu": 0.82, "larga_std": 0.07, "pesada_mu": 0.70, "pesada_std": 0.10, "impru_alpha": 3, "impru_beta": 9},
    "Calculador_Audaz": {"prob": 0.05, "exp_mu": 22, "exp_std": 5, "exp_min": 15, "exp_max": 30, "fiab_alpha": 10, "fiab_beta": 4, "larga_mu": 0.90, "larga_std": 0.06, "pesada_mu": 0.92, "pesada_std": 0.05, "impru_alpha": 5.5, "impru_beta": 5},
    "Novato": {"prob": 0.02, "exp_mu": 1.5, "exp_std": 1, "exp_min": 0, "exp_max": 3, "fiab_alpha": 4, "fiab_beta": 5, "larga_mu": 0.65, "larga_std": 0.15, "pesada_mu": 0.55, "pesada_std": 0.15, "impru_alpha": 8, "impru_beta": 4}
}

NOMBRES_POOL = ["Juan", "Carlos", "José", "Luis", "Miguel", "Jorge", "Eduardo", "Roberto", "Pedro", "Sergio"]
APELLIDOS_POOL = ["González", "Rodríguez", "López", "García", "Gómez", "Fernández", "Díaz", "Martínez", "Pérez", "Romero"]

# ============================================================
# HELPER FUNCTIONS
# ============================================================

def sanitizar_email(texto):
    """Removes accents, weird characters, spaces, and lowers case for production safety."""
    # Split characters from their accent marks (NFD normalization)
    texto_normalizado = unicodedata.normalize('NFD', texto)
    # Filter out the accent marks (combining characters) and keep alphanumeric/dots/underscores
    texto_limpio = "".join(ch for ch in texto_normalizado if unicodedata.category(ch) != 'Mn')
    # Final cleanup to remove remaining illegal characters or spaces
    texto_limpio = texto_limpio.replace(" ", "").replace("ñ", "n").replace("Ñ", "n")
    return texto_limpio.lower()

def calcular_cuit_valido(dni_base):
    """Generates a valid Argentine CUIT string from a numeric base."""
    prefijo = 99  # 99 para diferenciar de los "Reales"
    factores = [5, 4, 3, 2, 7, 6, 5, 4, 3, 2]
    cuit_base = f"{prefijo}{str(dni_base).zfill(8)}"
    
    suma = sum(int(cuit_base[i]) * factores[i] for i in range(10))
    resto = suma % 11
    
    if resto == 0:
        dv = 0
    elif resto == 1:
        # Casos especiales de AFIP
        prefijo = 23
        cuit_base = f"{prefijo}{str(dni_base).zfill(8)}"
        suma = sum(int(cuit_base[i]) * factores[i] for i in range(10))
        resto = suma % 11
        dv = 0 if resto == 0 else 11 - resto
    else:
        dv = 11 - resto
        
    return f"{prefijo}-{dni_base}-{dv}"

def calcular_fecha_inicio(anios_experiencia):
    """Calculates an approximate startDate based on simulated experience."""
    dias_totales = int(anios_experiencia * 365.25)
    fecha_calculada = datetime.now() - timedelta(days=dias_totales)
    return fecha_calculada.strftime('%Y-%m-%d')

# ============================================================
# MAIN DATA GENERATION PROCESS
# ============================================================
def generar_dataset_produccion(cantidad=100):
    print("Iniciando la generación unificada de datos de producción...")
    
    lista_choferes = []
    conteo_tipos = {nombre: 0 for nombre in PERSONALIDADES.keys()}
    tipos = list(PERSONALIDADES.keys())
    probs = [PERSONALIDADES[p]["prob"] for p in tipos]
    
    # Pool de DNI base incremental para evitar colisiones de CUIT
    dni_inicial = 30214500

    for idx in range(cantidad):
        # 1. Determinar el Tipo/Personalidad del Conductor
        tipo = np.random.choice(tipos, p=probs)
        conteo_tipos[tipo] += 1
        p = PERSONALIDADES[tipo]
        
        # 2. Generar Atributos Escondidos (Mapeo de lógicas previas)
        exp = round(np.clip(np.random.normal(p["exp_mu"], p["exp_std"]), p["exp_min"], p["exp_max"]), 1)
        fiab = round(np.random.beta(p["fiab_alpha"], p["fiab_beta"]), 4)
        afin_larga = round(np.clip(np.random.normal(p["larga_mu"], p["larga_std"]), 0.0, 1.0), 4)
        afin_pesada = round(np.clip(np.random.normal(p["pesada_mu"], p["pesada_std"]), 0.0, 1.0), 4)
        impru = round(np.random.beta(p["impru_alpha"], p["impru_beta"]), 4)
        
        # 3. Simular Métricas de Rendimiento Histórico (Lógica ex script 2)
        score = np.clip((exp * 2.3) + (fiab * 48) + ((1 - impru) * 28) + (afin_larga * 10) + (afin_pesada * 8), 28, 97)
        
        total_ordenes = int(np.clip(np.random.normal(exp * 13 + 75, 48), 45, 480))
        
        prob_larga = np.clip(0.22 + (afin_larga - 0.5) * 0.38, 0.12, 0.58)
        prob_corta = np.clip(0.38 + (1 - afin_larga) * 0.32, 0.28, 0.62)
        prob_media = 1.0 - prob_larga - prob_corta
        
        prob_pesada = np.clip(0.42 + (afin_pesada - 0.5) * 0.36, 0.28, 0.72)
        prob_liviana = 1.0 - prob_pesada
        
        # Probabilidades de Éxito
        p_corta = np.clip((0.935 + (score / 100) * 0.055) * (1 - impru * 0.12), 0.68, 0.99)
        p_media = np.clip((0.82 + (score / 100) * 0.14) * (1 - impru * 0.28), 0.52, 0.96)
        p_larga = np.clip((0.68 + (score / 100) * 0.22) * (0.94 + (afin_larga * 0.12)) * (1 - impru * 0.45), 0.38, 0.93)
        p_pesada_mult = 0.89
        
        # Simular viajes individuales
        np.random.seed(idx) # Mantener la consistencia por chofer
        tipos_dist = np.random.choice(['larga', 'media', 'corta'], size=total_ordenes, p=[prob_larga, prob_media, prob_corta])
        tipos_peso = np.random.choice(['pesada', 'liviana'], size=total_ordenes, p=[prob_pesada, prob_liviana])
        
        largas, largas_ok = 0, 0
        medias, medias_ok = 0, 0
        cortas, cortas_ok = 0, 0
        pesadas, pesadas_ok = 0, 0
        
        for i in range(total_ordenes):
            # Evaluar por distancia
            if tipos_dist[i] == 'corta':
                p_viaje = p_corta
                cortas += 1
            elif tipos_dist[i] == 'media':
                p_viaje = p_media
                medias += 1
            else:
                p_viaje = p_larga
                largas += 1
                
            # Evaluar por Peso
            if tipos_peso[i] == 'pesada':
                p_viaje *= p_pesada_mult
                pesadas += 1
            
            # Ejecutar simulación del viaje
            es_exitoso = np.random.rand() < p_viaje
            if es_exitoso:
                if tipos_dist[i] == 'corta': cortas_ok += 1
                elif tipos_dist[i] == 'media': medias_ok += 1
                else: largas_ok += 1
                if tipos_peso[i] == 'pesada': pesadas_ok += 1

        livianas = total_ordenes - pesadas
        livianas_ok = (cortas_ok + medias_ok + largas_ok) - pesadas_ok
        
        # --- NUEVA MÉTRICA PRODUCTIVA ---
        # Incidencias graves derivadas directamente del factor de imprudencia simulado
        incidencias_graves = int(np.random.poisson(impru * 4))
        
        # 4. Generación de Atributos del Perfil de Producción (Esquema Java)
        nombre_completo = f"{random.choice(NOMBRES_POOL)} {random.choice(APELLIDOS_POOL)}"
        email_base = nombre_completo.lower().replace(" ", ".")
        
        usuario_email = sanitizar_email(f"{email_base}_{idx+1}@ejemplar-ml.com")
        usuario_legajo = f"ML-{str(1000 + idx + 1)}"
        tipo_vinculo_nombre = random.choice(["Empleado", "Monotributo", "Tercerizado"])
        cuit = calcular_cuit_valido(dni_inicial + idx)
        empresa_nombre = "Maquina Logística S.A."
        disponible = 1 if random.random() < 0.94 else 0  # 94% de la flota activa
        inicio_actividad = calcular_fecha_inicio(exp)
        
        # Mapear al Schema Java
        registro_produccion = {
            "usuario_email": usuario_email,
            "usuario_legajo": usuario_legajo,
            "tipo_vinculo_nombre": tipo_vinculo_nombre,
            "cuit": cuit,
            "empresa_nombre": empresa_nombre,
            "disponible": disponible,
            "inicio_actividad": inicio_actividad,
            "total_ordenes": total_ordenes,
            "largas": largas,
            "largas_exitosas": largas_ok,
            "medias": medias,
            "medias_exitosas": medias_ok,
            "cortas": cortas,
            "cortas_exitosas": cortas_ok,
            "pesadas": pesadas,
            "pesadas_exitosas": pesadas_ok,
            "livianas": livianas,
            "livianas_exitosas": livianas_ok,
            "incidencias_graves": incidencias_graves
        }
        lista_choferes.append(registro_produccion)

    # 5. Generar DataFrame y exportar sin IDs internos redundantes de ML
    df_final = pd.DataFrame(lista_choferes)
    df_final.to_csv(OUTPUT_PRODUCTION_CSV, index=False, encoding='utf-8')
    
    print(f"\n✅ Dataset generado correctamente para Java en: '{OUTPUT_PRODUCTION_CSV}'")
    print(f"Registros procesados: {len(df_final)}")
    print("\nEstructura de las primeras líneas:")
    print(df_final.head(3).T) # Transpuesto para visualizar fácilmente en terminal

if __name__ == "__main__":
    generar_dataset_produccion(CANTIDAD_CHOFERES)