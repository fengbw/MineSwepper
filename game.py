import random

mine = []
mark = []
guess = []
stack = []
width = 30
height = 16

def createMap(probability):
    mine = [[None] * width for i in range(height)]
    mark = [[None] * width for i in range(height)]
    guess = [[0] * width for i in range(height)]
    for row in range(height):
        for col in range(width):
            r = random.random()
            if r < probability:
                mine[row][col] = -1
            else:
                mine[row][col] = 0
        #print(myList[row])

    for row in range(height):
        for col in range(width):
            if mine[row][col] == -1:
                continue
            sum = 0
            if row - 1 >= 0 and col - 1 >= 0 and mine[row - 1][col - 1] == -1:
                sum += 1
            if row - 1 >= 0 and mine[row - 1][col] == -1:
                sum += 1
            if row - 1 >= 0 and col + 1 < width and mine[row - 1][col + 1] == -1:
                sum += 1
            if col - 1 >= 0 and mine[row][col - 1] == -1:
                sum += 1
            if col + 1 < width and mine[row][col + 1] == -1:
                sum += 1
            if row + 1 < height and col - 1 >= 0 and mine[row + 1][col - 1] == -1:
                sum += 1
            if row + 1 < height and mine[row + 1][col] == -1:
                sum += 1
            if row + 1 < height and col + 1 < width and mine[row + 1][col + 1] == -1:
                sum += 1
            mine[row][col] = sum
    for row in range(height):
        print(mine[row])

def straight():
    if len(stack) == 0:
        row = random.randint(0, height - 1)
        col = random.randint(0, width - 1)
        if mine[row][col] == -1:
            return False
        mark[row][col] = 0
        stack.append((row, col))
    while len(stack) > 0:
        row, col = stack.pop(0)
        if mine[row][col] == 0:
            for r in range(row - 1, row + 2):
                for c in range(col - 1, col + 2):
                    if 0 <= r and 0 <= c and r < height and c < width:
                        if mark[r][c] is None:
                            mark[r][c] = 0
                            stack.push((r,c))
        #mark = [[None] * width for i in range(height)]
        #mark[row][col] = 0
        if 1 <= mine[row][col] <= 8:
            boom_number = mine[row][col]
            block_white = 0
            block_banner = 0
            for r in range(row - 1, row + 2):
                for c in range(col - 1, col + 2):
                    if 0 <= r and 0 <= c and r < height and c < width:
                        if not (r == row and c == col):
                            if mark[r][c] == -1:
                                block_banner += 1
                            elif mark[r][c] is None:
                                block_white += 1
            if boom_number == (block_white + block_banner):
                for r in range(row - 1, row + 2):
                    for c in range(col - 1, col + 2):
                        if 0 <= r and 0 <= c and r < height and c < width:
                            if mark[r][c] is None:
                                mark[r][c] = -1
            if boom_number == block_banner:
                for r in range(row - 1, row + 2):
                    for c in range(col - 1, col + 2):
                        if 0 <= r and 0 <= c and r < height and c < width:
                            if mark[r][c] is None:
                                mark[r][c] = 0
                                stack.append((r, c))


def allVisited():
    for row in range(height):
        for col in range(width):
            if mark[row][col] is None:
                return False

def bestGuess():
    minGuess = float('inf')
    tempR, tempC = 0, 0
    for row in range(height):
        for col in range(width):
            if mark[row][col] == 0:
                boom_number = mine[row][col]
                block_white = 0
                block_banner = 0
                for r in range(row - 1, row + 2):
                    for c in range(col - 1, col + 2):
                        if 0 <= r and 0 <= c and r < height and c < width:
                            if not (r == row and c == col):
                                if mark[r][c] == -1:
                                    block_banner += 1
                                elif mark[r][c] is None:
                                    block_white += 1
                for r in range(row - 1, row + 2):
                    for c in range(col - 1, col + 2):
                        if 0 <= r and 0 <= c and r < height and c < width:
                            if not (r == row and c == col):
                                if mark[r][c] is None:
                                    guess[r][c] += (boom_number - block_banner) / block_white
                                    if guess[r][c] < minGuess:
                                        minGuess = guess[r][c]
                                        tempR = r
                                        tempC = C
    if mine[tempR][tempC] == -1:
        return False
    else:
        stack.append((tempR, tempC))

def agent():
    while not allVisited():
        straight()
        bestGuess()

def main():
    createMap(0.2)
    agent()

if __name__ == '__main__':
    main()
