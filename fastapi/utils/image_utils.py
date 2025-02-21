import cv2
import numpy as np

def crop_and_deskew(image, box):
    box = np.array(box, dtype=np.float32)
    width = int(np.linalg.norm(box[0] - box[1]))
    height = int(np.linalg.norm(box[1] - box[2]))
    if width < 10 or height < 10:
        return None
    dst_pts = np.array([[0, 0], [width - 1, 0], [width - 1, height - 1], [0, height - 1]], dtype=np.float32)
    M = cv2.getPerspectiveTransform(box, dst_pts)
    rotated = cv2.warpPerspective(image, M, (width, height))
    return rotated