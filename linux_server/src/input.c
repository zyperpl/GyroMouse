#include "input.h"
#include "data.h"


Display *display;
Window root_window;

struct {
  float x; float y; } vec2;


//TODO: add real screen resolution getters
int get_screen_width()
{
  return 1920;
}

int get_screen_height()
{
  return 1080;
}

void input_init()
{
  display = XOpenDisplay(0);
  root_window = XRootWindow(display, 0);
  XSelectInput(display, root_window, KeyReleaseMask);

  vec2.x = get_screen_width()/2;
  vec2.y = get_screen_height()/2;
}

void input_execute(struct sensor_data sensor)
{
  printf("Type: %c\n", sensor.type);

  switch (sensor.type) {
    case Gyroscope:
      input_gyroscope(sensor);
      break;
    case LeftClick:
      input_mouse_click(LMB);
      break;
    case RightClick:
      input_mouse_click(RMB);
      break;

    case Light:
      set_brightness(get_light_level(sensor));
      break;
    case Touch:
      input_touch(sensor);
      break;
  }
}

void input_touch(struct sensor_data sensor) 
{
  vec2.x += sensor.values[0]*4;
  vec2.y += sensor.values[1]*3;

  XWarpPointer(display, None, root_window, 0, 0, 0, 0, (int)(vec2.x+0.5), (int)(vec2.y+0.5));
  XFlush(display); 

}

void input_gyroscope(struct sensor_data sensor)
{
  vec2.y -= sensor.values[0]*20;
  vec2.x -= sensor.values[2]*20;

  XWarpPointer(display, None, root_window, 0, 0, 0, 0, (int)(vec2.x+0.5), (int)(vec2.y+0.5));
  XFlush(display); 
}

void input_mouse_click(int btn)
{
  XTestFakeButtonEvent(display, btn, 1, CurrentTime);
  XFlush(display);

  usleep(1000);

  XTestFakeButtonEvent(display, btn, 0, CurrentTime);
  XFlush(display);

}

void set_brightness(float value)
{
  char str[255];
  sprintf(str, "xbacklight -set %f", value);
  printf("%f\n", value);

  execute_cmd(str);
}

void execute_cmd(char *cmd)
{
  FILE *fp;

  /* Open the command for reading. */
  fp = popen(cmd, "r");
  if (fp == NULL) {
    printf("Failed to run command\n" );
    exit(1);
  }

  pclose(fp);    
}

float get_light_level(struct sensor_data sensor)
{
  if (sensor.type != Light) return -1.0F;

  float value = sensor.values[0];
  if (value > LIGHT_MAX) value = LIGHT_MAX;

  value = value/LIGHT_MAX*100;

  return value;
}
