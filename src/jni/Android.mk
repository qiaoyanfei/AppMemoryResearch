LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := native_memory

LOCAL_SRC_FILES :=native_memory.c stack_struct.c

include $(BUILD_SHARED_LIBRARY)