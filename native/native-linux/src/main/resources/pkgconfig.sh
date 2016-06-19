#!/bin/sh
LIB=$1
echo PKG.CONFIG.CFLAGS=`pkg-config --cflags $LIB`
echo PKG.CONFIG.LIBS=`pkg-config --libs --static $LIB`
