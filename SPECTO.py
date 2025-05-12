import tensorflow as tf
import numpy as np
import librosa
import os
import json

# Load trained model
model = tf.keras.models.load_model("bird_classifier_cnn.h5", compile=True)

# Audio processing parameters
SR = 22050
N_MELS = 128
FIXED_WIDTH = 200

def audio_to_spectrogram(audio, sr=SR, n_mels=N_MELS, fixed_width=FIXED_WIDTH):
    """Convert raw audio signal into a mel spectrogram with a fixed width."""
    spectrogram = librosa.feature.melspectrogram(y=audio, sr=sr, n_mels=n_mels)
    spectrogram_db = librosa.power_to_db(spectrogram, ref=np.max)

    if spectrogram_db.shape[1] < fixed_width:
        pad_width = fixed_width - spectrogram_db.shape[1]
        spectrogram_db = np.pad(spectrogram_db, ((0, 0), (0, pad_width)), mode='constant')
    else:
        spectrogram_db = spectrogram_db[:, :fixed_width]

    return spectrogram_db

def load_class_names():
    """Load class names from saved JSON file to ensure consistent mapping."""
    try:
        with open("class_names.json", "r") as f:
            class_names = json.load(f)
            print(f"✅ Loaded {len(class_names)} class names: {class_names}")
            return class_names
    except FileNotFoundError:
        print("🚨 ERROR: class_names.json not found! Ensure the training script saves it.")
        return []

def predict_bird_species(audio_file, model, class_names):
    """Predict the bird species from an audio file using the trained CNN model."""
    if not os.path.exists(audio_file):
        print(f"❌ ERROR: Audio file not found: {audio_file}")
        return None, None

    # Load and preprocess the audio file
    audio, _ = librosa.load(audio_file, sr=SR)
    spectrogram = audio_to_spectrogram(audio)

    # Ensure input shape matches CNN input (batch_size, height, width, channels)
    spectrogram = np.expand_dims(spectrogram, axis=(0, -1))

    # Debugging: Print shape of spectrogram
    print(f"🔍 Spectrogram Shape: {spectrogram.shape}")

    # Make predictions
    predictions = model.predict(spectrogram)

    # Debugging: Print raw predictions
    print(f"📊 Raw Predictions: {predictions}")

    predicted_index = np.argmax(predictions)

    # Ensure index is valid
    if predicted_index >= len(class_names):
        print(f"🚨 ERROR: Predicted index {predicted_index} is out of range! Check class mapping.")
        return None, None

    predicted_class = class_names[predicted_index]
    return predicted_class, predictions[0]

# Load class names safely
class_names = load_class_names()
if not class_names:
    print("❌ ERROR: No class names loaded. Exiting.")
    exit()

# Path to test audio (Ensure correct path format)
test_audio = r"D:\test_audio\XC625678.wav"

# Predict
predicted_species, confidence_scores = predict_bird_species(test_audio, model, class_names)

if predicted_species:
    print(f"✅ Predicted Bird Species: {predicted_species}")
    print(f"🔢 Confidence Scores: {confidence_scores}")
else:
    print("❌ Prediction failed. Check errors above.")

