// Pulseaudio driver for Nanotunes

#include <stdio.h>
#include "pulse/simple.h"
#include "kiss_fft.h"

#define STREAM_NAME "nlaudio"
#define STREAM_DESC "Audio visualizer"
#define SAMPLE_RATE 44100
#define SAMPLE_SIZE 4096
#define FFT_SIZE SAMPLE_SIZE*4

// Opens a pulseaudio stream for recording on the default output device.
pa_simple *pulse_open() {
    pa_sample_spec ss;
    
    ss.format = PA_SAMPLE_S16NE;
    ss.channels = 2;
    ss.rate = SAMPLE_RATE;
    
    return pa_simple_new(NULL,              // Server
                        STREAM_NAME,        // Stream name (user-facing)
                        PA_STREAM_RECORD,   // Stream direction
                        NULL,               // Device (NULL = default)
                        STREAM_DESC,        // Stream description (user-facing)
                        &ss,                // Sampling format
                        NULL,               // Channel map (NULL = default)
                        NULL,               // Buffering attributes (NULL = default)
                        NULL                // Error code handling (NULL = ignore)
                        );
}

// Closes the pulseaudio stream.
void pulse_close(pa_simple *pulse) {
    pa_simple_free(pulse);
}

// Returns the maximum amplitude in the transformed fourier wave.
int get_max_amplitude(pa_simple *pulse) {
    uint16_t data[SAMPLE_SIZE];
    kiss_fft_cfg cfg = kiss_fft_alloc(FFT_SIZE, 0, NULL, NULL);
    kiss_fft_cpx *in = calloc(FFT_SIZE, sizeof(kiss_fft_cpx));
    kiss_fft_cpx *out = calloc(FFT_SIZE, sizeof(kiss_fft_cpx));

    pa_simple_read(pulse, data, sizeof(data), NULL);
    for (int i = 0; i < SAMPLE_SIZE; i++) {
    	in[i + FFT_SIZE - SAMPLE_SIZE].r = (int16_t)data[i];
    }

    kiss_fft(cfg, in, out);
    
    int maxAmp = 0;
    for (int i = 1; i < FFT_SIZE / 2; i++) {
		if (out[i].r >= maxAmp) {
			maxAmp = out[i].r;
		}
	}

    kiss_fft_free(cfg);
    free(in);
    free(out);

    return maxAmp;
}
