import os

rootdir = r'C:\Users\Research\Documents\GitHub\battlecode2020'


for subdir, dirs, files in os.walk(rootdir):
    for file in files:
        full_path = os.path.join(subdir, file)
        try: 
            with open(full_path, 'r') as f: 
                for line in f: 
                    if 'maptestsmall' in line: 
                        print(file)
                        break

        finally: 
            skipped = True
