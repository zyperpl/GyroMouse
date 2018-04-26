#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SIZEOF_JAVA_CHAR  2
#define SIZEOF_JAVA_FLOAT 4

#define VALUES_NUM 3

#define PACKET_SIZE VALUES_NUM*SIZEOF_JAVA_FLOAT+2*SIZEOF_JAVA_CHAR


// assume little-endian

struct sensor_data
{
  char header;
  char type;
  double values[VALUES_NUM];
};

struct sensor_data convert_to_sensor_data(char *buf);

void print_sensor_data(struct sensor_data sensor);

float bytes_to_float(char *buf);
