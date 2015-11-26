/**
 * Native JNI bindings for he-profiler.
 *
 * @author Connor Imes
 * @date 2015-11-25
 */

#include <stdlib.h>
#include <string.h>
#include <jni.h>
// native lib headers
#include <he-profiler.h>
// auto-generated header
#include <he-profiler-wrapper.h>

// checking that function pointers are set ensures initialization
#define MACRO_GET_EVENT_OR_RETURN(ret) \
  he_profiler_event* event; \
  if (ptr == NULL) { \
    return ret; \
  } \
  event = (he_profiler_event*) (*env)->GetDirectBufferAddress(env, ptr); \
  if (event == NULL) { \
    return ret; \
  }

JNIEXPORT jint JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_init(JNIEnv* env,
                                                                          jobject obj,
                                                                          jint numProfilers,
                                                                          jint applicationProfiler,
                                                                          jobjectArray profilerNames,
                                                                          jlong defaultWindowSize,
                                                                          jstring envVarPrefix,
                                                                          jstring logPath) {
  int ret = 0;
  int i;
  jsize nnames = 0;
  const char** c_profilerNames = NULL;

  // get profiler names
  if (profilerNames != NULL) {
    nnames = (*env)->GetArrayLength(env, profilerNames);
    if (nnames > 0) {
      c_profilerNames = malloc(nnames * sizeof(char*));
      if (c_profilerNames != NULL) {
        for (i = 0; i < nnames; i++) {
          jstring string = (jstring) (*env)->GetObjectArrayElement(env, profilerNames, i);
          const char* name = (*env)->GetStringUTFChars(env, string, NULL);
          c_profilerNames[i] = name;
        }
      }
    }
  }

  // get environment variable prefix
  const char* c_envVarPrefix = NULL;
  if (envVarPrefix != NULL) {
    c_envVarPrefix = (*env)->GetStringUTFChars(env, envVarPrefix, NULL);
  }

  // get log path
  const char* c_logPath = NULL;
  if (logPath != NULL) {
    c_logPath = (*env)->GetStringUTFChars(env, logPath, NULL);
  }
  
  ret = he_profiler_init(numProfilers,
                         applicationProfiler,
                         c_profilerNames,
                         defaultWindowSize,
                         c_envVarPrefix,
                         c_logPath);

  // cleanup strings
  if (c_envVarPrefix != NULL) {
    (*env)->ReleaseStringUTFChars(env, envVarPrefix, c_envVarPrefix);
  }
  if (c_logPath != NULL) {
    (*env)->ReleaseStringUTFChars(env, logPath, c_logPath);
  }
  if (c_profilerNames != NULL) {
    for (i = 0; i < nnames; i++) {
      jstring string = (jstring) (*env)->GetObjectArrayElement(env, profilerNames, i);
      (*env)->ReleaseStringUTFChars(env, string, c_profilerNames[i]);
    }
    free(c_profilerNames);
  }

  return ret;
}

JNIEXPORT jint JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_finish(JNIEnv* env,
                                                                            jobject obj) {
  return he_profiler_finish();
}

/**
 * Allocate memory for an he_profiler_event.
 * Returns a pointer to the he_profiler_event, or NULL on failure.
 */
JNIEXPORT jobject JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventAlloc(JNIEnv* env,
                                                                                   jobject obj) {
  he_profiler_event* event = calloc(1, sizeof(he_profiler_event));
  if (event == NULL) {
    return NULL;
  }
  return (*env)->NewDirectByteBuffer(env, (void*) event, sizeof(he_profiler_event));
}

/**
 * Free the he_profiler_event specified by the provided pointer.
 */
JNIEXPORT void JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventFree(JNIEnv* env,
                                                                               jobject obj,
                                                                               jobject ptr) {
  if (ptr != NULL) {
    he_profiler_event* event = (he_profiler_event*) (*env)->GetDirectBufferAddress(env, ptr);
    free(event);
  }
}

JNIEXPORT jint JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventBegin(JNIEnv* env,
                                                                                jobject obj,
                                                                                jobject ptr) {
  MACRO_GET_EVENT_OR_RETURN(-1);
  he_profiler_event_begin(event);
  return 0;
}

JNIEXPORT jint JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventEnd(JNIEnv* env,
                                                                              jobject obj,
                                                                              jobject ptr,
                                                                              jint profiler,
                                                                              jlong id,
                                                                              jlong work) {
  MACRO_GET_EVENT_OR_RETURN(-1);
  he_profiler_event_end(profiler, id, work, event);
  return 0;
}

JNIEXPORT jint JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventEndBegin(JNIEnv* env,
                                                                                   jobject obj,
                                                                                   jobject ptr,
                                                                                   jint profiler,
                                                                                   jlong id,
                                                                                   jlong work) {
  MACRO_GET_EVENT_OR_RETURN(-1);
  he_profiler_event_end_begin(profiler, id, work, event);
  return 0;
}
