import sys, glob
sys.path.insert(0, glob.glob('D:/SOFTWARE/opencv/build/python/2.7/x64')[0])

import cv2

# local modules
from video import create_capture
from common import clock, draw_str


# Detect cascade (pattern) from image
def detect(img, cascade):
    rects = cascade.detectMultiScale(img, scaleFactor=1.3, minNeighbors=4, minSize=(30, 30),
                                     flags=cv2.CASCADE_SCALE_IMAGE)
    if len(rects) == 0:
        return []
    rects[:,2:] += rects[:,:2]
    return rects

# Draw rectangle from detected object
def draw_rects(img, rects, color):
    for x1, y1, x2, y2 in rects:
        cv2.rectangle(img, (x1, y1), (x2, y2), color, 2)

if __name__ == '__main__':
    import sys, getopt
    print(__doc__)
    
    # Try to get 1st argument as a source for video-file
    args, video_src = getopt.getopt(sys.argv[1:], '', ['cascade=', 'nested-cascade='])
    try:
        video_src = video_src[0]
    except:
        # If no video-file provide, then use webcam as video source
        video_src = 0    
    args = dict(args)
    
    # Default : use haarcascade algoritm to detect face and eyes
    face_fn = args.get('--cascade', "data/haarcascades/haarcascade_frontalface_alt.xml")
    left_eye_fn  = args.get('--nested-cascade', "data/haarcascades/haarcascade_lefteye_2splits.xml")
    right_eye_fn  = args.get('--nested-cascade', "data/haarcascades/haarcascade_righteye_2splits.xml")

    # Define the classifier for each algorithm
    face = cv2.CascadeClassifier(face_fn)
    left_eye = cv2.CascadeClassifier(left_eye_fn)
    right_eye = cv2.CascadeClassifier(right_eye_fn)

    # Capture video from source (either webcam or video-file)
    cam = create_capture(video_src)

    while True:
        # Capture each frame as image
        ret, vis = cam.read()
        # Convert image to gray
        gray = cv2.cvtColor(vis, cv2.COLOR_BGR2GRAY)
        gray = cv2.equalizeHist(gray)
        # Detect face from image
        rects = detect(gray, face)
        # Draw face rectangle in image with green border
        draw_rects(vis, rects, (0, 255, 0))
        # Read face rectangle coordinates
        for face_x1, face_y1, face_x2, face_y2 in rects:
            # Draw face label
            draw_str(vis, (face_x1, face_y1), 'Face')
            # Get face image
            face_gray = gray[face_y1:face_y2, face_x1:face_x2]
            vis_face = vis[face_y1:face_y2, face_x1:face_x2]
            
            center_of_leye = None
            # detect left eye coordinates in face image
            left_eye_rect = detect(face_gray, left_eye)
            # Draw left eye rectangle in face boundary with blue border
            draw_rects(vis_face, left_eye_rect, (255, 0, 0))
            # Read left eye rectangle coordinates
            for leye_x1, leye_y1, leye_x2, leye_y2 in left_eye_rect:
                # Draw left eye label
                draw_str(vis_face, (leye_x1, leye_y1), 'Left eye')
                # Calculate the center of left eye
                center_of_leye = leye_x1 + ((leye_x2 - leye_x1) / 2)
            
            center_of_reye = None
            # detect right eye coordinates in face image
            right_eye_rect = detect(face_gray, right_eye)
            # Draw right eye rectangle in face boundary with red border
            draw_rects(vis_face, right_eye_rect, (0, 0, 255))
            # Read right eye rectangle coordinates
            for reye_x1, reye_y1, reye_x2, reye_y2 in right_eye_rect:
                # Draw right eye label
                draw_str(vis_face, (reye_x1, reye_y1), 'Right eye')
                # Calculate the center of right eye
                center_of_reye = reye_x1 + ((reye_x2 - reye_x1) / 2)
            
            # Label of status will be placed in the bottom of face rectangle
            label_pos = (face_x1, face_y2)
            # If both left eye and right eye are not detected
            if center_of_leye is None and center_of_reye is None:
                draw_str(vis, label_pos, 'Eyes not detected')
            # If only left eye is not detected -> Look left
            if center_of_leye is None:
                draw_str(vis, label_pos, 'Look left')
            # If only right eye is not detected -> Look right
            elif center_of_reye is None:
                draw_str(vis, label_pos, 'Look right')
            # If both are detected
            else:
                if abs((center_of_leye - face_x1) - (face_x2 - center_of_reye)) < 100:
                    draw_str(vis, label_pos, 'Look forward')
                # if right eye is more center than left
                elif (center_of_leye - face_x1) > (face_x2 - center_of_reye):
                    draw_str(vis, label_pos, 'Look left')
                # If left eye is more center than right
                else:
                    draw_str(vis, label_pos, 'Look right')
        
        # Show image
        cv2.imshow('facedetect', vis)

        if 0xFF & cv2.waitKey(5) == 27:
            break
    cv2.destroyAllWindows()

    
