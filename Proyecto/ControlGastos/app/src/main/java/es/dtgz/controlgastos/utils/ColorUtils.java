package es.dtgz.controlgastos.utils;

import android.graphics.Color;

public class ColorUtils {

    public static int getColorBasedOnValue(float amount, float targetValue) {
        // Definir colores de referencia
        int colorLow = Color.GREEN; // Color para valores bajos
        int colorHigh = Color.RED;  // Color para valores altos

        // Limitar el valor máximo al 100% del objetivo
        float normalizedAmount = Math.min(amount / targetValue, 1.0f);

        // Interpolación entre colores bajo y alto
        return interpolateColor(colorLow, colorHigh, normalizedAmount);
    }

    public static int getColorBasedOnValueAhorros(float amount, float targetValue) {
        // Definir colores de referencia para "Ahorros"
        int colorLow = Color.RED;   // Color para valores bajos (menos de objetivo)
        int colorHigh = Color.GREEN; // Color para valores altos (alcanzado o superado el objetivo)

        // Normalizar el monto respecto al valor objetivo
        float normalizedAmount = Math.min(amount / targetValue, 1.0f);

        // Interpolación entre colores bajo y alto
        return interpolateColor(colorLow, colorHigh, normalizedAmount);
    }

    private static int interpolateColor(int colorStart, int colorEnd, float fraction) {
        int startA = Color.alpha(colorStart);
        int startR = Color.red(colorStart);
        int startG = Color.green(colorStart);
        int startB = Color.blue(colorStart);

        int endA = Color.alpha(colorEnd);
        int endR = Color.red(colorEnd);
        int endG = Color.green(colorEnd);
        int endB = Color.blue(colorEnd);

        int alpha = (int) (startA + (endA - startA) * fraction);
        int red = (int) (startR + (endR - startR) * fraction);
        int green = (int) (startG + (endG - startG) * fraction);
        int blue = (int) (startB + (endB - startB) * fraction);

        return Color.argb(alpha, red, green, blue);
    }
}
