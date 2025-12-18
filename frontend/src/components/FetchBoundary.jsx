import Alert from './Alert';
import Spinner from './Spinner';

export default function FetchBoundary({ loading, error, children }) {
  if (loading) {
    return <Spinner />;
  }
  if (error) {
    return <Alert>{error.message}</Alert>;
  }
  return children;
}
