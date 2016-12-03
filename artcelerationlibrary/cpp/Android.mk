LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := image-transforms
LOCAL_SRC_FILES := image-transforms.cpp
LOCAL_LDLIBS    := -lm -llog -ljnigraphics
//LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)