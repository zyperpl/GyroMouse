#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <unistd.h>
#include <X11/X.h> 
#include <X11/Xlib.h> 
#include <X11/Xutil.h>
#include <X11/extensions/XTest.h>

#define LMB 1
#define RMB 3

#define LIGHT_MAX 2000

struct sensor_data;

enum InputType {
  LeftClick = 'A', RightClick, Gyroscope, Accelerometer, Light, Touch, MAX_INPUT
};

int get_screen_width();
int get_screen_height();

void input_init();
void input_gyroscope(struct sensor_data sensor);
void input_execute(struct sensor_data sensor);
void input_mouse_click(int btn);
void input_touch(struct sensor_data sensor);

void set_brightness(float value);
float get_light_level(struct sensor_data sensor);
void execute_cmd(char *cmd);
