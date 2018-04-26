#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>

#include "data.h"
#include "input.h"

#define PORT 1366
#define BUF_LEN 512

#define CMD "ip route get to %s | cut -f 5 -d ' ' | head -n 1"

#define SCAN_MAGIC "pl.zyper.gyroscopemouse.scan_servers_01"
#define SLEEP_MS 1200

int main(int argc, char *argv[])
{
  printf("Starting...\n\n");
  struct sockaddr_in si_me, si_other;
   
  int s, slen = sizeof(si_other), recv_len;
  char buf[BUF_LEN];
  
  // create socket
  if ((s=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1)
  {
    fprintf(stderr, "socket() error!\n");
    exit(1);
  }
   
  memset((char *) &si_me, 0, sizeof(si_me));
   
  si_me.sin_family = AF_INET;
  si_me.sin_port = htons(PORT);
  si_me.sin_addr.s_addr = htonl(INADDR_ANY);
   
  // bind socket
  if (bind(s, (struct sockaddr*)&si_me, sizeof(si_me)) == -1)
  {
    fprintf(stderr, "bind() error!\n");
    exit(1);
  }

  // initialize input
  input_init();

  for(;;)
  {
    printf("Listening for data...\n");
    fflush(stdout);
     
    memset(buf, 0, BUF_LEN);
    // wait for data
    if ((recv_len = recvfrom(s, buf, BUF_LEN, 0, (struct sockaddr *) &si_other, &slen)) == -1)
    {
      fprintf(stderr, "recvfrom() error!\n");
      exit(2);
    }
     
    printf("Data received from %s:%d\n", inet_ntoa(si_other.sin_addr), ntohs(si_other.sin_port));
    //printf("Data: %s (size: %d bytes)\n" , buf, recv_len); for (int i = 0; i < recv_len; i++) printf("%x ", (buf[i])); puts("");

 
    // if scan magic received
    if (strcmp(buf, SCAN_MAGIC) == 0)
    {
      printf("SCAN_MAGIC received. Sending SCAN_MAGIC...\n");

      usleep(SLEEP_MS * 1000);

      // change port to server port
      si_other.sin_port = htons(PORT);
       
      // send magic
      if (sendto(s, buf, strlen(buf), 0, (struct sockaddr*) &si_other, slen) == -1)
      {
        fprintf(stderr, "sendto() error!\n");
        exit(2);
      }
    } else
    {
      if (recv_len > 0)
      {
        struct sensor_data sensor = convert_to_sensor_data(buf);
        print_sensor_data(sensor);

        // execute actions based on data received
        if (sensor.type >= LeftClick && sensor.type < MAX_INPUT)
        {
          input_execute(sensor);
        }
      }
    }
  }

  close(s);
  return 0;
}
