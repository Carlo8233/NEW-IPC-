import os
import numpy as np
import librosa
import tensorflow as tf
from tensorflow.keras import layers, models
from sklearn.model_selection import train_test_split

SR = 22050  
N_MELS = 128 
FIXED_WIDTH = 200  

def load_audio(file_path, sr=SR):
    audio, _ = librosa.load(file_path, sr=sr)
    return audio

def audio_to_spectrogram(audio, sr=SR, n_mels=N_MELS, fixed_width=FIXED_WIDTH):
    spectrogram = librosa.feature.melspectrogram(y=audio, sr=sr, n_mels=n_mels)
    spectrogram_db = librosa.power_to_db(spectrogram, ref=np.max)

    if spectrogram_db.shape[1] < fixed_width:
        pad_width = fixed_width - spectrogram_db.shape[1]
        spectrogram_db = np.pad(spectrogram_db, ((0, 0), (0, pad_width)), mode='constant')
    else:
        spectrogram_db = spectrogram_db[:, :fixed_width]

    return spectrogram_db

def process_audio_files(data_dir):
    spectrograms = []
    labels = []

    class_names = [d for d in os.listdir(data_dir) if os.path.isdir(os.path.join(data_dir, d))]
    class_dict = {name: i for i, name in enumerate(class_names)}

    for class_name in class_names:
        class_dir = os.path.join(data_dir, class_name)

        for file in os.listdir(class_dir):
            file_path = os.path.join(class_dir, file)

            if os.path.isfile(file_path) and file_path.endswith(('.wav', '.mp3', '.flac')): 
                audio = load_audio(file_path)
                spectrogram = audio_to_spectrogram(audio)

                spectrograms.append(spectrogram)
                labels.append(class_dict[class_name])

    spectrograms = np.array(spectrograms)[..., np.newaxis]  
    labels = np.array(labels)
    return spectrograms, labels, class_names

# Load dataset
data_dir = "D:/Bird Noises"
spectrograms, labels, class_names = process_audio_files(data_dir)

# Train-test split
X_train, X_test, y_train, y_test = train_test_split(spectrograms, labels, test_size=0.2, random_state=42)

# Model Architecture (Improved)
model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(N_MELS, FIXED_WIDTH, 1)),
    layers.BatchNormalization(),  # Stabilizes training
    layers.MaxPooling2D((2, 2)),
    layers.Dropout(0.3),  # Reduces overfitting

    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.BatchNormalization(),
    layers.MaxPooling2D((2, 2)),
    layers.Dropout(0.3),

    layers.Conv2D(128, (3, 3), activation='relu'),
    layers.BatchNormalization(),
    layers.MaxPooling2D((2, 2)),
    layers.Dropout(0.4),

    layers.Flatten(),
    layers.Dense(128, activation='relu'),
    layers.Dropout(0.4),
    layers.Dense(len(class_names), activation='softmax')
])

# Compile model with lower learning rate
optimizer = tf.keras.optimizers.Adam(learning_rate=0.0001)  
model.compile(optimizer=optimizer, loss='sparse_categorical_crossentropy', metrics=['accuracy'])

# Early stopping callback
early_stopping = tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)

# Train the model
history = model.fit(X_train, y_train, epochs=50, validation_data=(X_test, y_test), callbacks=[early_stopping])

# Save model
model.save("bird_classifier_cnn.h5")

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open("bird_identifier.tflite", "wb") as f:
    f.write(tflite_model)

print("Model training and saving complete!")

# Plot Accuracy Graph
import matplotlib.pyplot as plt

plt.plot(history.history['accuracy'], 'bo-', label='Training Accuracy')
plt.plot(history.history['val_accuracy'], 'r^-', label='Validation Accuracy')
plt.title('CNN Model Accuracy Over Epochs')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()
plt.show()
