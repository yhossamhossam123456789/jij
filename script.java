document.addEventListener('DOMContentLoaded', () => {
    const board = document.getElementById('game-board');
    let grid = createGrid();
    let currentTetromino = createTetromino();
    let timerId;
  
    function createGrid() {
      const grid = [];
      for (let row = 0; row < 20; row++) {
        grid.push([]);
        for (let col = 0; col < 10; col++) {
          grid[row].push(0);
        }
      }
      return grid;
    }
  
    function createTetromino() {
      // Define different tetromino shapes
      const tetrominos = [
        [[1, 1, 1, 1]],
        [[1, 1], [1, 1]],
        [[1, 1, 1], [0, 1, 0]],
        [[1, 1, 1], [1, 0, 0]],
        [[1, 1, 0], [0, 1, 1]],
        [[0, 1, 1], [1, 1, 0]],
        [[1, 1, 1], [0, 0, 1]]
      ];
  
      // Randomly select a tetromino shape
      const randomIndex = Math.floor(Math.random() * tetrominos.length);
      const tetromino = tetrominos[randomIndex];
  
      // Set the initial position of the tetromino at the top center of the grid
      const col = Math.floor((10 - tetromino[0].length) / 2);
      return { shape: tetromino, row: 0, col };
    }
  
    function draw() {
      // Clear the previous position of the tetromino
      for (let row = 0; row < grid.length; row++) {
        for (let col = 0; col < grid[row].length; col++) {
          if (grid[row][col] === 1) {
            const block = document.getElementById(`block-${row}-${col}`);
            block.classList.remove('tetromino', 'tetromino-I', 'tetromino-O', 'tetromino-T', 'tetromino-S', 'tetromino-Z', 'tetromino-J', 'tetromino-L');
          }
        }
      }
  
      // Draw the tetromino at its current position
      for (let row = 0; row < currentTetromino.shape.length; row++) {
        for (let col = 0; col < currentTetromino.shape[row].length; col++) {
          if (currentTetromino.shape[row][col] === 1) {
            const block = document.getElementById(`block-${currentTetromino.row + row}-${currentTetromino.col + col}`);
            block.classList.add('tetromino', `tetromino-${getTetrominoType()}`);
          }
        }
      }
    }
  
    function getTetrominoType() {
      // Map tetromino shapes to their corresponding types
      const types = ['I', 'O', 'T', 'S', 'Z', 'J', 'L'];
      const index = tetrominos.findIndex(tetromino => tetromino === currentTetromino.shape);
      return types[index];
    }
  
    function moveDown() {
      currentTetromino.row++;
  
      if (collision()) {
        // If collision, revert the move and place the tetromino on the board
        currentTetromino.row--;
        placeTetromino();
        clearRows();
        currentTetromino = createTetromino();
        if (collision()) {
          // If collision after placing a new tetromino, the game is over
          clearInterval(timerId);
          alert('Game Over!');
          location.reload(); // Reload the page to restart the game
        }
      }
  
      draw();
    }
  
    function placeTetromino() {
      for (let row = 0; row < currentTetromino.shape.length; row++) {
        for (let col = 0; col < currentTetromino.shape[row].length; col++) {
          if (currentTetromino.shape[row][col] === 1) {
            grid[currentTetromino.row + row][currentTetromino.col + col] = 1;
          }
        }
      }
    }
  
    function collision() {
      // Check for collisions with the boundaries of the grid and other placed blocks
      for (let row = 0; row < currentTetromino.shape.length; row++) {
        for (let col = 0; col < currentTetromino.shape[row].length; col++) {
          if (
            (currentTetromino.shape[row][col] === 1 &&
              (currentTetromino.row + row >= 20 || currentTetromino.col + col < 0 || currentTetromino.col + col >= 10 ||
                grid[currentTetromino.row + row][currentTetromino.col + col] === 1))
          ) {
            return true;
          }
        }
      }
      return false;
    }
  
    function clearRows() {
      // Check and clear completed rows
      for (let row = grid.length - 1; row >= 0; row--) {
        if (grid[row].every(cell => cell === 1)) {
          // Remove the completed row
          grid.splice(row, 1);
          // Add a new empty row at the top
          grid.unshift(Array(10).fill(0));
        }
      }
    }
  
    function rotate() {
      const originalTetromino = currentTetromino.shape;
      currentTetromino.shape = rotateMatrix(currentTetromino.shape);
  
      if (collision()) {
        // If collision after rotation, revert the rotation
        currentTetromino.shape = originalTetromino;
      }
  
      draw();
    }
  
    function rotateMatrix(matrix) {
      // Rotate a 2D matrix 90 degrees clockwise
      const result = [];
      const rows = matrix.length;
      const cols = matrix[0].length;
  
      for (let col = 0; col < cols; col++) {
        result.push([]);
        for (let row = rows - 1; row >= 0; row--) {
          result[col].push(matrix[row][col]);
        }
      }
  
      return result;
    }
  
    function startGame() {
      // Initialize the game and set up the game loop
      draw();
      timerId = setInterval(moveDown, 1000);
    }
  
    document.addEventListener('keydown', (event) => {
      switch (event.key) {
        case 'ArrowLeft':
          currentTetromino.col--;
          if (collision()) {
            // If collision, revert the move
            currentTetromino.col++;
          }
          break;
        case 'ArrowRight':
          currentTetromino.col++;
          if (collision()) {
            // If collision, revert the move
            currentTetromino.col--;
          }
          break;
        case 'ArrowDown':
          moveDown();
          break;
        case 'ArrowUp':
          rotate();
          break;
      }
  
      draw();
    });
  
    startGame();
  });
  