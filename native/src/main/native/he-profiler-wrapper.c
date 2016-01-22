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
                                                                          jobjectArray profilerNames,
                                                                          jlongArray windowSizes,
                                                                          jlong defaultWindowSize,
                                                                          jint applicationProfiler,
                                                                          jlong appProfilerMinSleepUs,
                                                                          jstring logPath) {
  int ret = 0;
  int i, j;
  jsize nnames = 0;
  jstring string;
  const char** c_profilerNames = NULL;

  if (numProfilers == 0) {
    return -1;
  }

  // get window sizes
  jsize nwindows = 0;
  jlong* c_window_sizes = NULL;
  if (windowSizes != NULL) {
    nwindows = (*env)->GetArrayLength(env, windowSizes);
    if (nwindows != numProfilers) {
      return -1;
    }
    c_window_sizes = (*env)->GetLongArrayElements(env, windowSizes, 0);
  }

  // get profiler names
  if (profilerNames != NULL) {
    nnames = (*env)->GetArrayLength(env, profilerNames);
    // length of names array must match the number of profilers
    if (nnames != numProfilers) {
      if (c_window_sizes != NULL) {
        (*env)->ReleaseLongArrayElements(env, windowSizes, c_window_sizes, 0);
      }
      return -1;
    }
    c_profilerNames = malloc(nnames * sizeof(char*));
    if (c_profilerNames == NULL) {
      if (c_window_sizes != NULL) {
        (*env)->ReleaseLongArrayElements(env, windowSizes, c_window_sizes, 0);
      }
      return -1;
    }
    for (i = 0; i < nnames; i++) {
      string = (jstring) (*env)->GetObjectArrayElement(env, profilerNames, i);
      if (string == NULL) {
        c_profilerNames[i] = NULL;
      } else {
        const char* name = (*env)->GetStringUTFChars(env, string, NULL);
        if (name == NULL) {
          // failed to get C-compatible string from non-null Java string - need to cleanup
          for (j = 0; j < i; j++) {
            string = (jstring) (*env)->GetObjectArrayElement(env, profilerNames, j);
            if (string != NULL) {
              (*env)->ReleaseStringUTFChars(env, string, c_profilerNames[j]);
            }
          }
          if (c_window_sizes != NULL) {
            (*env)->ReleaseLongArrayElements(env, windowSizes, c_window_sizes, 0);
          }
          return -1;
        }
        c_profilerNames[i] = name;
      }
    }
  }

  // get log path
  const char* c_logPath = NULL;
  if (logPath != NULL) {
    c_logPath = (*env)->GetStringUTFChars(env, logPath, NULL);
  }
  
  ret = he_profiler_init(numProfilers,
                         c_profilerNames,
                         (uint64_t*) c_window_sizes,
                         defaultWindowSize,
                         applicationProfiler,
                         0,
                         c_logPath);

  // cleanup
  if (c_logPath != NULL) {
    (*env)->ReleaseStringUTFChars(env, logPath, c_logPath);
  }
  if (c_profilerNames != NULL) {
    for (i = 0; i < nnames; i++) {
      string = (jstring) (*env)->GetObjectArrayElement(env, profilerNames, i);
      if (string != NULL) {
        (*env)->ReleaseStringUTFChars(env, string, c_profilerNames[i]);
      }
    }
    free(c_profilerNames);
  }
  if (c_window_sizes != NULL) {
    (*env)->ReleaseLongArrayElements(env, windowSizes, c_window_sizes, 0);
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
 * Optionally, the event may begin (saves a JNI call).
 */
JNIEXPORT jobject JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventAlloc(JNIEnv* env,
                                                                                   jobject obj,
                                                                                   jboolean begin) {
  he_profiler_event* event = calloc(1, sizeof(he_profiler_event));
  if (event == NULL) {
    return NULL;
  }
  if (begin && he_profiler_event_begin(event)) {
    free(event);
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

/**
 * Return true on success, false otherwise.
 */
JNIEXPORT jboolean JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventBegin(JNIEnv* env,
                                                                                    jobject obj,
                                                                                    jobject ptr) {
  MACRO_GET_EVENT_OR_RETURN(JNI_FALSE);
  return he_profiler_event_begin(event) ? JNI_FALSE : JNI_TRUE;
}

/**
 * Return true on success, false otherwise.
 */
JNIEXPORT jboolean JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventEnd(JNIEnv* env,
                                                                                  jobject obj,
                                                                                  jobject ptr,
                                                                                  jint profiler,
                                                                                  jlong id,
                                                                                  jlong work,
                                                                                  jboolean isFree) {
  MACRO_GET_EVENT_OR_RETURN(JNI_FALSE);
  jboolean ret = he_profiler_event_end(event, profiler, id, work) ? JNI_FALSE : JNI_TRUE;
  if (isFree) {
    free(event);
  }
  return ret;
}

/**
 * Return true on success, false otherwise.
 */
JNIEXPORT jboolean JNICALL Java_edu_uchicago_cs_heprofiler_HEProfilerJNI_eventEndBegin(JNIEnv* env,
                                                                                       jobject obj,
                                                                                       jobject ptr,
                                                                                       jint profiler,
                                                                                       jlong id,
                                                                                       jlong work) {
  MACRO_GET_EVENT_OR_RETURN(JNI_FALSE);
  return he_profiler_event_end_begin(event, profiler, id, work) ? JNI_FALSE : JNI_TRUE;
}
