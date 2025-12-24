import { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { gameObjectsApi } from '../api/gameObjects';
import Alert from '../components/Alert';
import EmptyState from '../components/EmptyState';
import FetchBoundary from '../components/FetchBoundary';
import FormField from '../components/FormField';
import PageHeader from '../components/PageHeader';
import { ROUTES } from '../constants/routes';
import { useAuth } from '../context/AuthContext';
import { useFetch } from '../hooks/useFetch';
import { useGames } from '../hooks/useGames';
import { useSubmit } from '../hooks/useSubmit';
import { formatDate } from '../utils/format';

const EMPTY_FORM = { title: '', text: '', gameId: '' };

export default function GameObjectsPage() {
  const { isAuthenticated, isSeller } = useAuth();
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [actionError, setActionError] = useState(null);

  const { data: games } = useGames();
  const fetchObjects = useCallback((signal) => gameObjectsApi.list(signal), []);
  const { data: objects, loading, error, reload } = useFetch(fetchObjects);

  const saveObject = useCallback(
    (payload) => (editingId ? gameObjectsApi.update(editingId, payload) : gameObjectsApi.create(payload)),
    [editingId],
  );
  const { submit, pending, error: submitError } = useSubmit(saveObject);

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const saved = await submit({ ...form, gameId: Number(form.gameId) });
    if (saved !== undefined) {
      resetForm();
      reload();
    }
  };

  const handleEdit = (object) => {
    const game = games?.find((item) => item.name === object.gameName);
    setEditingId(object.id);
    setForm({ title: object.title, text: object.text, gameId: game ? String(game.id) : '' });
  };

  const handleDelete = async (id) => {
    setActionError(null);
    try {
      await gameObjectsApi.remove(id);
      reload();
    } catch (caught) {
      setActionError(caught);
    }
  };

  return (
    <>
      <PageHeader title="Game objects" subtitle="Items and offers published by sellers." />

      {isSeller ? (
        <form className="card form form--narrow" onSubmit={handleSubmit}>
          <h2 className="card__title">{editingId ? 'Edit object' : 'New object'}</h2>
          <FormField label="Title" name="title" required value={form.title} onChange={handleChange} />
          <FormField
            as="textarea"
            label="Description"
            name="text"
            rows="3"
            required
            value={form.text}
            onChange={handleChange}
          />
          <FormField as="select" label="Game" name="gameId" required value={form.gameId} onChange={handleChange}>
            <option value="" disabled>
              Select a game
            </option>
            {games?.map((game) => (
              <option key={game.id} value={game.id}>
                {game.name}
              </option>
            ))}
          </FormField>
          <Alert>{submitError?.message}</Alert>
          <div className="form__row form__row--actions">
            <button type="submit" className="btn" disabled={pending}>
              {editingId ? 'Save changes' : 'Add object'}
            </button>
            {editingId && (
              <button type="button" className="btn btn--ghost" onClick={resetForm}>
                Cancel
              </button>
            )}
          </div>
        </form>
      ) : (
        !isAuthenticated && (
          <Alert variant="info">
            <Link to={ROUTES.login}>Log in</Link> as a seller to publish your own objects.
          </Alert>
        )
      )}

      <Alert>{actionError?.message}</Alert>

      <FetchBoundary loading={loading} error={error}>
        {objects?.length ? (
          <div className="grid">
            {objects.map((object) => (
              <article key={object.id} className="card">
                <span className="tag">{object.gameName}</span>
                <h3 className="card__title">{object.title}</h3>
                <p className="muted">{object.text}</p>
                <footer className="card__footer">
                  <span>
                    {object.sellerName} · {formatDate(object.updatedAt ?? object.createdAt)}
                  </span>
                  {isSeller && (
                    <span className="card__actions">
                      <button type="button" className="btn btn--small btn--ghost" onClick={() => handleEdit(object)}>
                        Edit
                      </button>
                      <button
                        type="button"
                        className="btn btn--small btn--danger"
                        onClick={() => handleDelete(object.id)}
                      >
                        Delete
                      </button>
                    </span>
                  )}
                </footer>
              </article>
            ))}
          </div>
        ) : (
          <EmptyState title="Nothing here yet" description="Objects added by sellers will appear on this page." />
        )}
      </FetchBoundary>
    </>
  );
}
