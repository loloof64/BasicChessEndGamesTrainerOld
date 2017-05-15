/*
  Copyright (c) 2011-2013 Ronald de Man
  This file may be redistributed and/or modified without restrictions.

  tbcore.c contains engine-independent routines of the tablebase probing code.
  This file should not need too much adaptation to add tablebase probing to
  a particular engine, provided the engine is written in C or C++.
*/

// Moved all into tbprobe.cpp by Laurent Bernabe

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h>
#ifndef _WIN32
#include <unistd.h>
#include <sys/mman.h>
#endif
#include "tbcore.h"
