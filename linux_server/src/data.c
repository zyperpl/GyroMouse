#include "data.h"

struct sensor_data convert_to_sensor_data(char *buf)
{
  struct sensor_data data;

  const int SIZEOF_JAVA_HEADER = 2*SIZEOF_JAVA_CHAR;
  //const int SIZEOF_HEADER = 2*sizeof(char);

  memset(&data, 0, sizeof(struct sensor_data));

  data.header = (char)buf[0];
  data.type   = (char)buf[2];

  for (int i = 0; i < VALUES_NUM; i++) 
  {
    data.values[i] = bytes_to_float(buf+SIZEOF_JAVA_HEADER+i*SIZEOF_JAVA_FLOAT);
  }

  return data;
}

void print_sensor_data(struct sensor_data sensor)
{
  printf("[%c|%c] %5.5f, %5.5f, %5.5f\n", 
      sensor.header, sensor.type, 
      sensor.values[0], sensor.values[1], sensor.values[2]);
  printf("%5.5f\n", sensor.values[3]);
}

float bytes_to_float(char *buf)
{
  float f;

  memcpy(&f, buf, SIZEOF_JAVA_FLOAT);

  return f;
}
