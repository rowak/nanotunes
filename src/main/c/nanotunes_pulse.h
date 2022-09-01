#ifndef __nlaudio_h
#define __nlaudio_h

#include "pulse/simple.h"

pa_simple *pulse_open();
void pulse_close(pa_simple *pulse);
int get_max_amplitude(pa_simple *pulse);

#endif