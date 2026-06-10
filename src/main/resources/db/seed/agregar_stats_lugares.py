import pandas as pd
import numpy as np

# 1. Load your CSV file
# Replace 'your_file.csv' with your actual file path
df = pd.read_csv('src\\main\\resources\\db\\seed\\lugares_operativos.csv')

# 2. Set your target sums for each column
target_sum_recepciones = 9448 
target_sum_despachos = 9448 

# Initialize the new columns with 0 as the default
df['recepciones'] = 0
df['despachos'] = 0

# 3. Calculate how many rows are allowed to receive/dispatch
num_can_receive = df['puede_recibir'].sum()
num_can_dispatch = df['puede_despachar'].sum()

# 4. Distribute the target integer sums only among the valid rows
if num_can_receive > 0:
    # Generate random integers that sum exactly to target_sum_recepciones
    probabilities = [1.0 / num_can_receive] * num_can_receive
    random_ints_rec = np.random.multinomial(target_sum_recepciones, probabilities)
    
    # Assign them only where puede_recibir is 1
    df.loc[df['puede_recibir'] == 1, 'recepciones'] = random_ints_rec

if num_can_dispatch > 0:
    # Generate random integers that sum exactly to target_sum_despachos
    probabilities = [1.0 / num_can_dispatch] * num_can_dispatch
    random_ints_des = np.random.multinomial(target_sum_despachos, probabilities)
    
    # Assign them only where puede_despachar is 1
    df.loc[df['puede_despachar'] == 1, 'despachos'] = random_ints_des

# 5. Save the result to a new CSV file
df.to_csv('src\\main\\resources\\db\\seed\\lugares_operativos_actualizado.csv', index=False)

print("Columns added with integer constraints successfully!")

# Verification check:
print(f"Total Recepciones Sum: {df['recepciones'].sum()} (Expected: {target_sum_recepciones})")
print(f"Total Despachos Sum: {df['despachos'].sum()} (Expected: {target_sum_despachos})")
print("\nSample of the results:")
print(df[['puede_recibir', 'recepciones', 'puede_despachar', 'despachos']].head(10))