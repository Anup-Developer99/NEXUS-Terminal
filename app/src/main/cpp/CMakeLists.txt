cmake_minimum_required(VERSION 3.22.1)
project(nexus_native LANGUAGES CXX)

add_library(nexus_native SHARED native-lib.cpp)
target_compile_features(nexus_native PRIVATE cxx_std_17)
