package org.terraform.utils;

import java.util.ArrayList;

public class PoissonDiskSampling {
    FastNoise noise = new FastNoise();
    float density;

    public PoissonDiskSampling(float densityInChunk, float ampl) {
        noise.SetGradientPerturbAmp(ampl);
        noise.SetFrequency(0.07f);
        this.density = densityInChunk;
    }

    public PoissonDiskSampling() {
        this(5, 20);
    }

    public ArrayList<Vector2f>  getPositionsInChunk(int chunkX, int chunkZ) {
        double i = 16 / Math.sqrt(density);

        int startX = chunkX * 16;
        int startZ = chunkZ * 16;

        startX += (i - (startX % i));
        startZ += (i - (startZ % i));

        ArrayList<Vector2f> points = new ArrayList<>();

        for (int x = startX; x < (chunkX << 4) + 16; x += i) {
            for (int z = startZ; z < (chunkZ << 4) + 16; z += i) {
                Vector2f point = new Vector2f(Math.round(x), Math.round(z));
                gradientPerturb(point);
                points.add(point);
            }
        }

        return points;
    }

    public void gradientPerturb(Vector2f v) {
        noise.GradientPerturb(v);
    }
}
