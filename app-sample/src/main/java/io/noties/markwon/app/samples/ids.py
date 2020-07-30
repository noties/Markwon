# script used to fix generated ids of samples (`DD` was used in pattern
# instead of `dd`, so day in year instead of day in month)

import os
import re
from datetime import datetime


def samples(array, directory):
    for f in os.listdir(directory):
        name = os.path.join(directory, f)
        if os.path.isdir(name):
            samples(array, name)
        elif name.endswith("java") or name.endswith("kt"):
            array.append(name)


def process(sample):
    with open(sample, "r+") as f:
        code = f.read()
        m = re.search(
            r"(?:\s\S)*@MarkwonSampleInfo\(\s*id\s*\=\s*?\"(\d+)\"(?:\s\S)*", code)
        if not m:
            print(
                "File {} does not contain `@MarkwonSampleInfo` annotation".format(sample))
        else:
            group = m.group(1)
            # `%j` is day in year modifier
            # for day in year we take substring starting when month ends (year and month are fixed width)
            #   and end excluding hour, minute and second (also fixed)
            day = datetime.strptime("{} {}".format(
                group[6:-6], group[0:4]), "%j %Y").day
            id = group[:6] + "{:02d}".format(day) + group[-6:]
            print("{} `{}` -> `{}`".format(sample, group, id))
            updated_code = code.replace(group, id, 1)

            # https://stackoverflow.com/a/15976014
            f.seek(0)
            f.write(updated_code)
            f.truncate()


if __name__ == '__main__':
    array = []
    samples(array, ".")
    for sample in array:
        process(sample)
