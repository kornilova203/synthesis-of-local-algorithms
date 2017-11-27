import pycosat
import sys


def parse_clauses_from_file(file_name):
    with open(file_name) as f:
        first_line = f.readline()
        var_count, clause_count = [int(x) for x in first_line.split()[2:4]]
        clauses = []
        for line in f:  # read rest of lines
            clauses.append([int(x) for x in line.split()])
        assert clause_count == len(clauses)
        return clauses, False


def parse_clauses_from_stdin():
    first_line = sys.stdin.readline()
    find_all = False
    if first_line == "Find all solutions\n":
        find_all = True
        first_line = sys.stdin.readline()
    var_count, clause_count = [int(x) for x in first_line.split()[2:4]]
    clauses = []
    for line in sys.stdin:
        clauses.append([int(x) for x in line.split()])
    assert clause_count == len(clauses)
    return clauses, find_all


def main():
    if len(sys.argv) == 2:
        clauses, find_all = parse_clauses_from_file(sys.argv[1])
    else:
        clauses, find_all = parse_clauses_from_stdin()
    if find_all:
        for res in pycosat.itersolve(clauses):
            if isinstance(res, list):
                print("OK")
                print(" ".join([str(x) for x in res]))
            else:
                print(res)
    else:
        res = pycosat.solve(clauses)
        if isinstance(res, list):
            print("OK")
            print(" ".join([str(x) for x in res]))
        else:
            print(res)


main()
