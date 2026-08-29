package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.models.ProjectProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

  @Query("SELECT * FROM projects WHERE language = :language ORDER BY difficulty ASC, id ASC")
  fun getProjectsForLanguage(language: String): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects ORDER BY id ASC")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE id = :projectId")
  suspend fun getProjectById(projectId: String): ProjectEntity?

  @Query("SELECT * FROM projects WHERE id = :projectId")
  fun observeProjectById(projectId: String): Flow<ProjectEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjects(projects: List<ProjectEntity>)

  @Query("UPDATE projects SET isCompleted = 1, completedAtEpochMs = :completedAt WHERE id = :projectId")
  suspend fun markProjectCompleted(projectId: String, completedAt: Long = System.currentTimeMillis())

  @Query("UPDATE projects SET isUnlocked = :isUnlocked WHERE id = :projectId")
  suspend fun setProjectUnlocked(projectId: String, isUnlocked: Boolean)

  // Project Files
  @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isMain DESC, fileName ASC")
  fun getFilesForProject(projectId: String): Flow<List<ProjectFileEntity>>

  @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isMain DESC, fileName ASC")
  suspend fun getFilesForProjectOnce(projectId: String): List<ProjectFileEntity>

  @Query("SELECT * FROM project_files WHERE projectId = :projectId AND fileName = :fileName")
  suspend fun getFile(projectId: String, fileName: String): ProjectFileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateFile(file: ProjectFileEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFiles(files: List<ProjectFileEntity>)

  @Query("DELETE FROM project_files WHERE projectId = :projectId AND fileName = :fileName")
  suspend fun deleteFile(projectId: String, fileName: String)

  @Query("DELETE FROM project_files WHERE projectId = :projectId")
  suspend fun deleteAllFilesForProject(projectId: String)

  // Project Progress
  @Query("SELECT * FROM project_progress WHERE projectId = :projectId")
  suspend fun getProjectProgress(projectId: String): ProjectProgressEntity?

  @Query("SELECT * FROM project_progress WHERE projectId = :projectId")
  fun observeProjectProgress(projectId: String): Flow<ProjectProgressEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveProjectProgress(progress: ProjectProgressEntity)

  @Transaction
  suspend fun resetProjectToStarterFiles(projectId: String, starterFiles: Map<String, String>) {
    deleteAllFilesForProject(projectId)
    val files = starterFiles.map { (name, content) ->
      ProjectFileEntity(
        projectId = projectId,
        fileName = name,
        fileContent = content,
        isMain = name == "main.py" || name == "index.js" || name == "index.html",
        isReadOnly = name.endsWith(".md", ignoreCase = true),
        lastModifiedEpochMs = System.currentTimeMillis()
      )
    }
    insertFiles(files)
  }
}
