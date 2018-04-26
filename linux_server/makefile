EXECUTABLE = gyromouse

CC         = gcc
FLAGS      = -Wall -I ./src/ -ggdb

LIBS       = -lstdc++ -lm -lX11 -lXtst

SRC_PATH   = ./src
OBJ_PATH   = ./obj

SOURCES  := $(wildcard $(SRC_PATH)/*.c)
INCLUDES := $(wildcard $(SRC_PATH)/*.h)
OBJECTS  := $(SOURCES:$(SRC_PATH)/%.c=$(OBJ_PATH)/%.o)

$(EXECUTABLE): $(OBJECTS) 
	@echo "Linking..."
	$(CC) -o $@ $(OBJECTS) $(LIBS) $(FLAGS) -o $(EXECUTABLE)
	@echo "Done!"

$(OBJECTS): $(OBJ_PATH)/%.o: $(SRC_PATH)/%.c 
	@echo "Compiling $@..."
	$(CC) -c -o $@ $< $(FLAGS)


.PHONY: clean
clean:
	rm -rf obj/*
	sleep 0.1s
	mkdir -p obj/
	sleep 0.1s
	mkdir -p src/
	rm $(EXECUTABLE)	
	@echo "Cleaned!"

